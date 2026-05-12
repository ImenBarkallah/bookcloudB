package com.bookcloud.smartlibrary.serviceimpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.AdminReservationDetailDto;
import com.bookcloud.smartlibrary.dto.AdminReservationListItemDto;
import com.bookcloud.smartlibrary.dto.ReservationHistoryLineDto;
import com.bookcloud.smartlibrary.enums.HistoryEventType;
import com.bookcloud.smartlibrary.enums.ReservationStatus;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.LibraryBranch;
import com.bookcloud.smartlibrary.model.LibraryHistoryEntry;
import com.bookcloud.smartlibrary.model.Reservation;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.LibraryBranchRepository;
import com.bookcloud.smartlibrary.repository.LibraryHistoryRepository;
import com.bookcloud.smartlibrary.repository.LibrarySettingsRepository;
import com.bookcloud.smartlibrary.repository.ReservationRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.LoanService;
import com.bookcloud.smartlibrary.service.ReservationService;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	private final LibraryBranchRepository libraryBranchRepository;
	private final LibrarySettingsRepository librarySettingsRepository;
	private final LibraryHistoryRepository libraryHistoryRepository;
	private final LoanService loanService;

	public ReservationServiceImpl(
			ReservationRepository reservationRepository,
			BookRepository bookRepository,
			UserRepository userRepository,
			LibraryBranchRepository libraryBranchRepository,
			LibrarySettingsRepository librarySettingsRepository,
			LibraryHistoryRepository libraryHistoryRepository,
			LoanService loanService) {
		this.reservationRepository = reservationRepository;
		this.bookRepository = bookRepository;
		this.userRepository = userRepository;
		this.libraryBranchRepository = libraryBranchRepository;
		this.librarySettingsRepository = librarySettingsRepository;
		this.libraryHistoryRepository = libraryHistoryRepository;
		this.loanService = loanService;
	}

	@Override
	public Reservation create(Long bookId, String userUid, Long pickupBranchId) {
		Book book = resolveBook(bookId);
		if (book.getAvailableCopies() > 0) {
			throw new BusinessRuleException("Book is available for direct borrowing");
		}
		if (reservationRepository.findFirstByUserUidAndBookIdAndStatusIn(userUid, bookId,
				List.of(ReservationStatus.PENDING, ReservationStatus.APPROVED)).isPresent()) {
			throw new BusinessRuleException("Reservation already exists for this book");
		}
		AppUser user = resolveUser(userUid);
		LibraryBranch branch = pickupBranchId != null ? libraryBranchRepository.findById(pickupBranchId)
				.orElseThrow(() -> new ResourceNotFoundException("Branch not found")) : null;
		Reservation reservation = new Reservation();
		reservation.setBook(book);
		reservation.setUser(user);
		reservation.setPickupBranch(branch);
		reservation.setStatus(ReservationStatus.PENDING);
		reservation.setQueuePosition((int) reservationRepository.countByBook_IdAndStatus(bookId, ReservationStatus.PENDING) + 1);
		reservation.setExpiresAt(Instant.now().plus(settings().getReservationExpiryDays(), ChronoUnit.DAYS));
		Reservation saved = reservationRepository.save(reservation);
		appendHistory(saved, HistoryEventType.RESERVATION_CREATED, "Reservation created: " + safeBookTitle(book));
		return saved;
	}

	@Override
	public Reservation cancel(Long reservationId, String actingUid, Role callerRole) {
		Reservation reservation = getByIdForCaller(reservationId, actingUid, callerRole);
		if (reservation.getStatus() != ReservationStatus.PENDING && reservation.getStatus() != ReservationStatus.APPROVED) {
			throw new BusinessRuleException("Reservation can no longer be cancelled");
		}
		reservation.setStatus(ReservationStatus.CANCELLED);
		reservation.setQueuePosition(null);
		Reservation saved = reservationRepository.save(reservation);
		appendHistory(saved, HistoryEventType.RESERVATION_CANCELLED, "Reservation cancelled: " + safeBookTitle(saved.getBook()));
		recomputeQueue(saved.getBook());
		return saved;
	}

	@Override
	public Reservation approve(Long reservationId, Role callerRole) {
		requireStaff(callerRole);
		Reservation reservation = resolveReservation(reservationId);
		if (reservation.getStatus() != ReservationStatus.PENDING) {
			throw new BusinessRuleException("Reservation is already processed");
		}
		List<Reservation> queue = reservationRepository.findByBook_IdAndStatusOrderByCreatedAtAsc(
				reservation.getBookId(),
				ReservationStatus.PENDING);
		if (queue.isEmpty() || !queue.get(0).getId().equals(reservationId)) {
			throw new BusinessRuleException("Reservation is not first in queue");
		}
		if (reservation.getBook().getAvailableCopies() <= 0) {
			throw new BusinessRuleException("Book is still unavailable");
		}
		reservation.setStatus(ReservationStatus.APPROVED);
		reservation.setExpiresAt(Instant.now().plus(settings().getReservationExpiryDays(), ChronoUnit.DAYS));
		Reservation saved = reservationRepository.save(reservation);
		appendHistory(saved, HistoryEventType.RESERVATION_APPROVED, "Reservation approved: " + safeBookTitle(saved.getBook()));
		recomputeQueue(saved.getBook());
		return saved;
	}

	@Override
	public Reservation reject(Long reservationId, Role callerRole) {
		requireStaff(callerRole);
		Reservation reservation = resolveReservation(reservationId);
		if (reservation.getStatus() != ReservationStatus.PENDING && reservation.getStatus() != ReservationStatus.APPROVED) {
			throw new BusinessRuleException("Reservation already processed");
		}
		reservation.setStatus(ReservationStatus.CANCELLED);
		reservation.setQueuePosition(null);
		Reservation saved = reservationRepository.save(reservation);
		appendHistory(saved, HistoryEventType.RESERVATION_REJECTED, "Reservation rejected: " + safeBookTitle(saved.getBook()));
		recomputeQueue(saved.getBook());
		return saved;
	}

	@Override
	public Long convertToLoan(Long reservationId, Role callerRole) {
		requireStaff(callerRole);
		Reservation reservation = resolveReservation(reservationId);
		if (reservation.getStatus() != ReservationStatus.APPROVED) {
			throw new BusinessRuleException("Reservation must be approved before conversion");
		}
		Long loanId = loanService.borrow(reservation.getBookId(), reservation.getUserUid(), reservation.getPickupBranchId(), null)
				.getId();
		appendHistory(reservation, HistoryEventType.RESERVATION_CONVERTED_TO_LOAN,
				"Reservation converted to loan: " + safeBookTitle(reservation.getBook()));
		reservationRepository.delete(reservation);
		recomputeQueue(reservation.getBook());
		return loanId;
	}

	@Override
	public Reservation complete(Long reservationId, Role callerRole) {
		return approve(reservationId, callerRole);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Reservation> listForUser(String userUid) {
		return reservationRepository.findByUser_UidOrderByCreatedAtDesc(userUid).stream().map(this::expireIfNeeded).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Reservation> listAll() {
		return reservationRepository.findAllDetailed().stream().map(this::expireIfNeeded).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AdminReservationListItemDto> listAllForAdmin() {
		return reservationRepository.findAllDetailed().stream().map(this::expireIfNeeded).map(this::toAdminDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public AdminReservationDetailDto getAdminDetail(Long reservationId, String actingUid, Role role) {
		Reservation reservation = getByIdForCaller(reservationId, actingUid, role);
		AdminReservationDetailDto dto = new AdminReservationDetailDto();
		copyAdminDto(toAdminDto(reservation), dto);
		dto.setHistory(libraryHistoryRepository.findByReferenceIdOrderByOccurredAtAsc(reservation.getId()).stream()
				.map(this::toHistoryLine)
				.toList());
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public Reservation getByIdForCaller(Long id, String actingUid, Role role) {
		Reservation reservation = resolveReservation(id);
		if (!reservation.getUserUid().equals(actingUid) && role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new AccessDeniedException("Reservation access denied");
		}
		return expireIfNeeded(reservation);
	}

	private Reservation resolveReservation(Long id) {
		return reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
	}

	private AppUser resolveUser(String uid) {
		return userRepository.findByUid(uid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private Book resolveBook(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found"));
	}

	private void requireStaff(Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new AccessDeniedException("Staff access required");
		}
	}

	private Reservation expireIfNeeded(Reservation reservation) {
		if (reservation.getStatus() == ReservationStatus.PENDING
				&& reservation.getExpiresAt() != null
				&& reservation.getExpiresAt().isBefore(Instant.now())) {
			reservation.setStatus(ReservationStatus.EXPIRED);
			reservation.setQueuePosition(null);
			Reservation saved = reservationRepository.save(reservation);
			appendHistory(saved, HistoryEventType.RESERVATION_EXPIRED,
					"Reservation expired: " + safeBookTitle(saved.getBook()));
			recomputeQueue(saved.getBook());
			return saved;
		}
		return reservation;
	}

	private void recomputeQueue(Book book) {
		List<Reservation> pending = reservationRepository.findByBook_IdAndStatusOrderByCreatedAtAsc(book.getId(),
				ReservationStatus.PENDING);
		for (int i = 0; i < pending.size(); i++) {
			Reservation item = pending.get(i);
			item.setQueuePosition(i + 1);
			reservationRepository.save(item);
		}
	}

	private void appendHistory(Reservation reservation, HistoryEventType type, String summary) {
		LibraryHistoryEntry entry = new LibraryHistoryEntry();
		entry.setUser(reservation.getUser());
		entry.setBook(reservation.getBook());
		entry.setReferenceId(reservation.getId());
		entry.setType(type);
		entry.setSummary(summary);
		entry.setOccurredAt(Instant.now());
		libraryHistoryRepository.save(entry);
	}

	private AdminReservationListItemDto toAdminDto(Reservation reservation) {
		AdminReservationListItemDto dto = new AdminReservationListItemDto();
		dto.setId(reservation.getId());
		dto.setBookId(reservation.getBookId());
		dto.setBookTitle(safeBookTitle(reservation.getBook()));
		dto.setBookAuthor(reservation.getBook() != null ? reservation.getBook().getAuthor() : "");
		dto.setCoverUrl(buildCoverUrl(reservation.getBook()));
		dto.setUserUid(reservation.getUserUid());
		dto.setUserDisplayName(reservation.getUser() != null ? reservation.getUser().getDisplayName() : null);
		dto.setUserEmail(reservation.getUser() != null ? reservation.getUser().getEmail() : null);
		dto.setReservedAt(reservation.getReservedAt());
		dto.setExpiresAt(reservation.getExpiresAt());
		dto.setQueuePosition(reservation.getQueuePosition());
		dto.setStatus(reservation.getStatus().name());
		dto.setPickupBranchId(reservation.getPickupBranchId());
		return dto;
	}

	private void copyAdminDto(AdminReservationListItemDto from, AdminReservationDetailDto to) {
		to.setId(from.getId());
		to.setBookId(from.getBookId());
		to.setBookTitle(from.getBookTitle());
		to.setBookAuthor(from.getBookAuthor());
		to.setCoverUrl(from.getCoverUrl());
		to.setUserUid(from.getUserUid());
		to.setUserDisplayName(from.getUserDisplayName());
		to.setUserEmail(from.getUserEmail());
		to.setReservedAt(from.getReservedAt());
		to.setExpiresAt(from.getExpiresAt());
		to.setQueuePosition(from.getQueuePosition());
		to.setStatus(from.getStatus());
		to.setPickupBranchId(from.getPickupBranchId());
	}

	private ReservationHistoryLineDto toHistoryLine(LibraryHistoryEntry entry) {
		ReservationHistoryLineDto dto = new ReservationHistoryLineDto();
		dto.setId(entry.getId());
		dto.setType(entry.getType() != null ? entry.getType().name() : "");
		dto.setSummary(entry.getSummary());
		dto.setOccurredAt(entry.getOccurredAt());
		return dto;
	}

	private com.bookcloud.smartlibrary.model.LibrarySettings settings() {
		return librarySettingsRepository.findTopByOrderByIdAsc()
				.orElseThrow(() -> new ResourceNotFoundException("Library settings not found"));
	}

	private String safeBookTitle(Book book) {
		return book != null && book.getTitle() != null ? book.getTitle() : "Book";
	}

	private String buildCoverUrl(Book book) {
		if (book == null) {
			return null;
		}
		if (book.getCoverUrl() != null && !book.getCoverUrl().isBlank()) {
			return book.getCoverUrl();
		}
		if (book.getIsbn() == null || book.getIsbn().isBlank()) {
			return null;
		}
		String digits = book.getIsbn().replace("-", "").replace(" ", "");
		return digits.isBlank() ? null : "https://covers.openlibrary.org/b/isbn/" + digits + "-M.jpg";
	}
}
