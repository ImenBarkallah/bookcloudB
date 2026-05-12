package com.bookcloud.smartlibrary.serviceimpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.AdminLoanDetailDto;
import com.bookcloud.smartlibrary.dto.AdminLoanListItemDto;
import com.bookcloud.smartlibrary.dto.LoanHistoryLineDto;
import com.bookcloud.smartlibrary.dto.UserLoanItemDto;
import com.bookcloud.smartlibrary.enums.BookCopyStatus;
import com.bookcloud.smartlibrary.enums.FineStatus;
import com.bookcloud.smartlibrary.enums.HistoryEventType;
import com.bookcloud.smartlibrary.enums.LoanStatus;
import com.bookcloud.smartlibrary.enums.ReservationStatus;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.enums.TransactionRecordType;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.BookCopy;
import com.bookcloud.smartlibrary.model.Fine;
import com.bookcloud.smartlibrary.model.LibraryHistoryEntry;
import com.bookcloud.smartlibrary.model.LibrarySettings;
import com.bookcloud.smartlibrary.model.Loan;
import com.bookcloud.smartlibrary.model.Reservation;
import com.bookcloud.smartlibrary.model.TransactionRecord;
import com.bookcloud.smartlibrary.repository.BookCopyRepository;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.FineRepository;
import com.bookcloud.smartlibrary.repository.LibraryHistoryRepository;
import com.bookcloud.smartlibrary.repository.LibrarySettingsRepository;
import com.bookcloud.smartlibrary.repository.LoanRepository;
import com.bookcloud.smartlibrary.repository.ReservationRepository;
import com.bookcloud.smartlibrary.repository.TransactionRecordRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.LoanService;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

	private final LoanRepository loanRepository;
	private final BookRepository bookRepository;
	private final BookCopyRepository bookCopyRepository;
	private final UserRepository userRepository;
	private final LibrarySettingsRepository librarySettingsRepository;
	private final FineRepository fineRepository;
	private final LibraryHistoryRepository libraryHistoryRepository;
	private final TransactionRecordRepository transactionRecordRepository;
	private final ReservationRepository reservationRepository;

	public LoanServiceImpl(
			LoanRepository loanRepository,
			BookRepository bookRepository,
			BookCopyRepository bookCopyRepository,
			UserRepository userRepository,
			LibrarySettingsRepository librarySettingsRepository,
			FineRepository fineRepository,
			LibraryHistoryRepository libraryHistoryRepository,
			TransactionRecordRepository transactionRecordRepository,
			ReservationRepository reservationRepository) {
		this.loanRepository = loanRepository;
		this.bookRepository = bookRepository;
		this.bookCopyRepository = bookCopyRepository;
		this.userRepository = userRepository;
		this.librarySettingsRepository = librarySettingsRepository;
		this.fineRepository = fineRepository;
		this.libraryHistoryRepository = libraryHistoryRepository;
		this.transactionRecordRepository = transactionRecordRepository;
		this.reservationRepository = reservationRepository;
	}

	@Override
	public Loan borrow(Long bookId, String userUid, Long branchId, Long copyId) {
		AppUser user = resolveUser(userUid);
		validateBorrowRules(user);
		Book book = resolveBook(bookId);
		if (book.getAvailableCopies() <= 0) {
			throw new BusinessRuleException("No copies available");
		}

		BookCopy copy = null;
		if (copyId != null) {
			copy = bookCopyRepository.findById(copyId)
					.orElseThrow(() -> new ResourceNotFoundException("Copy not found"));
			if (!bookId.equals(copy.getBookId())) {
				throw new BusinessRuleException("Copy does not belong to the selected book");
			}
			if (copy.getStatus() != BookCopyStatus.AVAILABLE) {
				throw new BusinessRuleException("Copy is not available");
			}
		} else {
			List<BookCopy> availableCopies = bookCopyRepository.findByBook_IdAndStatusOrderByIdAsc(bookId,
					BookCopyStatus.AVAILABLE);
			if (!availableCopies.isEmpty()) {
				copy = availableCopies.stream()
						.filter(item -> branchId == null || branchId.equals(item.getBranchId()))
						.findFirst()
						.orElseThrow(() -> new BusinessRuleException("No available copy for this branch"));
			}
		}

		Loan loan = new Loan();
		loan.setBook(book);
		loan.setUser(user);
		loan.setBranch(copy != null ? copy.getBranch() : null);
		if (loan.getBranch() == null && branchId != null) {
			loan.setBranch(book.getDefaultBranch());
		}
		loan.setCopy(copy);
		loan.setBorrowedAt(Instant.now());
		loan.setDueAt(Instant.now().plus(settings().getDefaultLoanDays(), ChronoUnit.DAYS));
		loan.setStatus(LoanStatus.ACTIVE);
		loan.setRenewalCount(0);
		Loan saved = loanRepository.save(loan);

		book.setAvailableCopies(Math.max(0, book.getAvailableCopies() - 1));
		bookRepository.save(book);
		if (copy != null) {
			copy.setStatus(BookCopyStatus.ON_LOAN);
			bookCopyRepository.save(copy);
		}

		appendHistory(user, book, saved.getId(), HistoryEventType.LOAN_BORROWED, "Borrowed: " + safeBookTitle(book));
		appendTransaction(user, book, saved.getId(), TransactionRecordType.LOAN);
		return refreshLoanIfOverdue(saved);
	}

	@Override
	public Loan renewLoan(Long loanId, String actingUid, Role callerRole) {
		Loan loan = resolveLoan(loanId);
		assertLoanAccess(loan, actingUid, callerRole);
		if (loan.getStatus() != LoanStatus.ACTIVE) {
			throw new BusinessRuleException("Only active loans can be renewed");
		}
		int maxRenewals = settings().getMaxRenewalsPerLoan() != null ? settings().getMaxRenewalsPerLoan() : 0;
		if (loan.getRenewalCount() >= maxRenewals) {
			throw new BusinessRuleException("Maximum renewals reached");
		}
		Instant baseDue = loan.getDueAt() != null && loan.getDueAt().isAfter(Instant.now())
				? loan.getDueAt()
				: Instant.now();
		loan.setDueAt(baseDue.plus(settings().getDefaultLoanDays(), ChronoUnit.DAYS));
		loan.setRenewalCount(loan.getRenewalCount() + 1);
		return loanRepository.save(loan);
	}

	@Override
	public Loan returnLoan(Long loanId, String actingUid, Role callerRole) {
		Loan loan = resolveLoan(loanId);
		assertLoanAccess(loan, actingUid, callerRole);
		if (loan.getStatus() == LoanStatus.RETURNED) {
			throw new BusinessRuleException("Loan already returned");
		}
		loan.setReturnedAt(Instant.now());
		loan.setStatus(LoanStatus.RETURNED);
		Loan saved = loanRepository.save(loan);

		Book book = loan.getBook();
		book.setAvailableCopies(book.getAvailableCopies() + 1);
		bookRepository.save(book);

		if (loan.getCopy() != null) {
			BookCopy copy = loan.getCopy();
			copy.setStatus(BookCopyStatus.AVAILABLE);
			bookCopyRepository.save(copy);
		}

		appendHistory(loan.getUser(), book, saved.getId(), HistoryEventType.LOAN_RETURNED, "Returned: " + safeBookTitle(book));
		appendTransaction(loan.getUser(), book, saved.getId(), TransactionRecordType.RETURN);
		promoteNextReservation(book);
		return saved;
	}

	@Override
	@Transactional(readOnly = true)
	public Loan getByIdForCaller(Long loanId, String actingUid, Role role) {
		Loan loan = resolveLoan(loanId);
		assertLoanAccess(loan, actingUid, role);
		return refreshLoanIfOverdue(loan);
	}

	@Override
	public void deleteByStaff(Long loanId, Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new AccessDeniedException("Staff access required");
		}
		Loan loan = resolveLoan(loanId);
		if (loan.getStatus() != LoanStatus.RETURNED) {
			Book book = loan.getBook();
			book.setAvailableCopies(book.getAvailableCopies() + 1);
			bookRepository.save(book);
			if (loan.getCopy() != null) {
				BookCopy copy = loan.getCopy();
				copy.setStatus(BookCopyStatus.AVAILABLE);
				bookCopyRepository.save(copy);
			}
		}
		loanRepository.delete(loan);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Loan> listForUser(String userUid) {
		return loanRepository.findByUser_UidOrderByBorrowedAtDesc(userUid).stream().map(this::refreshLoanIfOverdue)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserLoanItemDto> listUserLoanItems(String userUid) {
		int maxRenewals = settings().getMaxRenewalsPerLoan() != null ? settings().getMaxRenewalsPerLoan() : 0;
		return listForUser(userUid).stream().map(loan -> toUserLoanItem(loan, maxRenewals)).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Loan> listAll() {
		return loanRepository.findAllForAdmin().stream().map(this::refreshLoanIfOverdue).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AdminLoanListItemDto> listAllForAdmin() {
		return loanRepository.findAllForAdmin().stream()
				.map(this::refreshLoanIfOverdue)
				.map(this::toAdminLoan)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public AdminLoanDetailDto getAdminLoanDetail(Long loanId, String actingUid, Role role) {
		Loan loan = getByIdForCaller(loanId, actingUid, role);
		AdminLoanDetailDto dto = new AdminLoanDetailDto();
		copyAdminLoan(toAdminLoan(loan), dto);
		dto.setHistory(libraryHistoryRepository.findByReferenceIdOrderByOccurredAtAsc(loan.getId()).stream()
				.map(this::toHistoryLine)
				.toList());
		return dto;
	}

	private AppUser resolveUser(String userUid) {
		return userRepository.findByUid(userUid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private Book resolveBook(Long bookId) {
		return bookRepository.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found"));
	}

	private Loan resolveLoan(Long loanId) {
		return loanRepository.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
	}

	private void validateBorrowRules(AppUser user) {
		if (user.isBlocked()) {
			throw new BusinessRuleException("Blocked users cannot borrow books");
		}
		if (user.getMembershipExpiresAt() != null && user.getMembershipExpiresAt().isBefore(Instant.now())) {
			throw new BusinessRuleException("Membership expired");
		}
		int activeLoans = (int) loanRepository.countByUser_UidAndStatusIn(
				user.getUid(),
				List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
		int limit = user.getMaxActiveLoans() != null && user.getMaxActiveLoans() > 0
				? user.getMaxActiveLoans()
				: settings().getMaxActiveLoansDefault();
		if (limit > 0 && activeLoans >= limit) {
			throw new BusinessRuleException("Active loan limit reached");
		}
	}

	private void assertLoanAccess(Loan loan, String actingUid, Role role) {
		boolean allowed = loan.getUserUid().equals(actingUid) || role == Role.ADMIN || role == Role.LIBRARIAN;
		if (!allowed) {
			throw new AccessDeniedException("Loan access denied");
		}
	}

	private LibrarySettings settings() {
		return librarySettingsRepository.findTopByOrderByIdAsc().orElseGet(() -> {
			LibrarySettings settings = new LibrarySettings();
			settings.setDefaultLoanDays(14);
			settings.setMaxActiveLoansDefault(5);
			settings.setReservationExpiryDays(7);
			settings.setMaxRenewalsPerLoan(2);
			settings.setOverdueFinePerDayCents(0L);
			settings.setFinePerDay(0D);
			return librarySettingsRepository.save(settings);
		});
	}

	private void appendHistory(AppUser user, Book book, Long referenceId, HistoryEventType type, String summary) {
		LibraryHistoryEntry entry = new LibraryHistoryEntry();
		entry.setUser(user);
		entry.setBook(book);
		entry.setReferenceId(referenceId);
		entry.setType(type);
		entry.setSummary(summary);
		entry.setOccurredAt(Instant.now());
		libraryHistoryRepository.save(entry);
	}

	private void appendTransaction(AppUser user, Book book, Long referenceId, TransactionRecordType type) {
		TransactionRecord record = new TransactionRecord();
		record.setType(type);
		record.setUser(user);
		record.setBook(book);
		record.setReferenceId(referenceId);
		record.setOccurredAt(Instant.now());
		transactionRecordRepository.save(record);
	}

	private void promoteNextReservation(Book book) {
		List<Reservation> queue = reservationRepository.findByBook_IdAndStatusOrderByCreatedAtAsc(book.getId(),
				ReservationStatus.PENDING);
		if (queue.isEmpty()) {
			return;
		}
		Reservation next = queue.get(0);
		next.setStatus(ReservationStatus.APPROVED);
		next.setExpiresAt(Instant.now().plus(settings().getReservationExpiryDays(), ChronoUnit.DAYS));
		reservationRepository.save(next);
	}

	private Loan refreshLoanIfOverdue(Loan loan) {
		if (loan.getStatus() == LoanStatus.RETURNED || loan.getDueAt() == null || !loan.getDueAt().isBefore(Instant.now())) {
			return loan;
		}
		if (loan.getStatus() == LoanStatus.ACTIVE) {
			loan.setStatus(LoanStatus.OVERDUE);
			loanRepository.save(loan);
			long rate = settings().getOverdueFinePerDayCents() != null ? settings().getOverdueFinePerDayCents() : 0L;
			if (rate > 0 && fineRepository.findFirstByLoan_IdAndStatus(loan.getId(), FineStatus.OPEN).isEmpty()) {
				long days = Math.max(1, ChronoUnit.DAYS.between(loan.getDueAt(), Instant.now()));
				Fine fine = new Fine();
				fine.setLoan(loan);
				fine.setUser(loan.getUser());
				fine.setAmountCents(days * rate);
				fine.setStatus(FineStatus.OPEN);
				fineRepository.save(fine);
			}
		}
		return loan;
	}

	private UserLoanItemDto toUserLoanItem(Loan loan, int maxRenewals) {
		UserLoanItemDto dto = new UserLoanItemDto();
		dto.setLoanId(loan.getId());
		dto.setBookId(loan.getBookId());
		dto.setBookTitle(safeBookTitle(loan.getBook()));
		dto.setBookAuthor(loan.getBook() != null ? loan.getBook().getAuthor() : "");
		dto.setCoverUrl(buildCoverUrl(loan.getBook()));
		dto.setStatus(loan.getStatus().name());
		dto.setBorrowedAt(loan.getBorrowedAt());
		dto.setDueAt(loan.getDueAt());
		dto.setReturnedAt(loan.getReturnedAt());
		dto.setRenewalCount(loan.getRenewalCount());
		dto.setMaxRenewals(maxRenewals);
		dto.setBranchId(loan.getBranchId());
		dto.setCanRenew(loan.getStatus() == LoanStatus.ACTIVE && loan.getRenewalCount() < maxRenewals);
		dto.setCanReturn(loan.getStatus() != LoanStatus.RETURNED);
		return dto;
	}

	private AdminLoanListItemDto toAdminLoan(Loan loan) {
		AdminLoanListItemDto dto = new AdminLoanListItemDto();
		dto.setId(loan.getId());
		dto.setBookId(loan.getBookId());
		dto.setBookTitle(safeBookTitle(loan.getBook()));
		dto.setBookAuthor(loan.getBook() != null ? loan.getBook().getAuthor() : "");
		dto.setCoverUrl(buildCoverUrl(loan.getBook()));
		dto.setUserUid(loan.getUserUid());
		dto.setUserDisplayName(loan.getUser() != null ? loan.getUser().getDisplayName() : null);
		dto.setUserEmail(loan.getUser() != null ? loan.getUser().getEmail() : null);
		dto.setBorrowedAt(loan.getBorrowedAt());
		dto.setDueAt(loan.getDueAt());
		dto.setReturnedAt(loan.getReturnedAt());
		dto.setStatus(loan.getStatus().name());
		dto.setBranchId(loan.getBranchId());
		dto.setCopyId(loan.getCopyId());
		dto.setRenewalCount(loan.getRenewalCount());
		return dto;
	}

	private void copyAdminLoan(AdminLoanListItemDto from, AdminLoanDetailDto to) {
		to.setId(from.getId());
		to.setBookId(from.getBookId());
		to.setBookTitle(from.getBookTitle());
		to.setBookAuthor(from.getBookAuthor());
		to.setCoverUrl(from.getCoverUrl());
		to.setUserUid(from.getUserUid());
		to.setUserDisplayName(from.getUserDisplayName());
		to.setUserEmail(from.getUserEmail());
		to.setBorrowedAt(from.getBorrowedAt());
		to.setDueAt(from.getDueAt());
		to.setReturnedAt(from.getReturnedAt());
		to.setStatus(from.getStatus());
		to.setBranchId(from.getBranchId());
		to.setCopyId(from.getCopyId());
		to.setRenewalCount(from.getRenewalCount());
	}

	private LoanHistoryLineDto toHistoryLine(LibraryHistoryEntry entry) {
		LoanHistoryLineDto dto = new LoanHistoryLineDto();
		dto.setId(entry.getId());
		dto.setType(entry.getType() != null ? entry.getType().name() : "");
		dto.setSummary(entry.getSummary());
		dto.setOccurredAt(entry.getOccurredAt());
		return dto;
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
