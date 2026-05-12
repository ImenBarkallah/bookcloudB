package com.bookcloud.smartlibrary.serviceimpl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.AdminCategoryCountDto;
import com.bookcloud.smartlibrary.dto.AdminDashboardOverviewDto;
import com.bookcloud.smartlibrary.dto.AdminMonthlySeriesPointDto;
import com.bookcloud.smartlibrary.dto.AdminTopBookDto;
import com.bookcloud.smartlibrary.dto.AdminTopUserDto;
import com.bookcloud.smartlibrary.enums.FineStatus;
import com.bookcloud.smartlibrary.enums.LoanStatus;
import com.bookcloud.smartlibrary.enums.ReservationStatus;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.Fine;
import com.bookcloud.smartlibrary.model.Loan;
import com.bookcloud.smartlibrary.model.Reservation;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.FavoriteBookRepository;
import com.bookcloud.smartlibrary.repository.FineRepository;
import com.bookcloud.smartlibrary.repository.LoanRepository;
import com.bookcloud.smartlibrary.repository.ReservationRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.AdminDashboardStatsService;

@Service
@Transactional(readOnly = true)
public class AdminDashboardStatsServiceImpl implements AdminDashboardStatsService {

	private final BookRepository bookRepository;
	private final LoanRepository loanRepository;
	private final ReservationRepository reservationRepository;
	private final FineRepository fineRepository;
	private final UserRepository userRepository;
	private final FavoriteBookRepository favoriteBookRepository;

	public AdminDashboardStatsServiceImpl(
			BookRepository bookRepository,
			LoanRepository loanRepository,
			ReservationRepository reservationRepository,
			FineRepository fineRepository,
			UserRepository userRepository,
			FavoriteBookRepository favoriteBookRepository) {
		this.bookRepository = bookRepository;
		this.loanRepository = loanRepository;
		this.reservationRepository = reservationRepository;
		this.fineRepository = fineRepository;
		this.userRepository = userRepository;
		this.favoriteBookRepository = favoriteBookRepository;
	}

	@Override
	public AdminDashboardOverviewDto overview() {
		List<Book> books = bookRepository.findAll();
		List<Loan> loans = loanRepository.findAll();
		List<Reservation> reservations = reservationRepository.findAll();
		List<Fine> fines = fineRepository.findAll();
		Instant startOfMonth = Instant.now().atZone(ZoneOffset.UTC).withDayOfMonth(1).toInstant();
		Instant startOfDay = Instant.now().atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);

		AdminDashboardOverviewDto dto = new AdminDashboardOverviewDto();
		dto.setTotalBooks(books.size());
		dto.setTotalCopies(books.stream().mapToLong(Book::getTotalCopies).sum());
		dto.setAvailableCopies(books.stream().mapToLong(Book::getAvailableCopies).sum());
		dto.setBorrowedCopies(dto.getTotalCopies() - dto.getAvailableCopies());
		dto.setTotalUsers(userRepository.count());
		dto.setNewUsersThisMonth(userRepository.findAll().stream().filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(startOfMonth)).count());
		dto.setActiveUsers(loans.stream().map(Loan::getUserUid).distinct().count());
		dto.setActiveLoans(loans.stream().filter(loan -> loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE).count());
		dto.setOverdueLoans(loans.stream().filter(loan -> loan.getStatus() == LoanStatus.OVERDUE).count());
		dto.setLoansToday(loans.stream().filter(loan -> loan.getBorrowedAt() != null && loan.getBorrowedAt().isAfter(startOfDay)).count());
		dto.setPendingReservations(reservations.stream().filter(reservation -> reservation.getStatus() == ReservationStatus.PENDING).count());
		dto.setApprovedReservations(reservations.stream().filter(reservation -> reservation.getStatus() == ReservationStatus.APPROVED).count());
		dto.setTotalFines(fines.size());
		dto.setOpenFines(fines.stream().filter(fine -> fine.getStatus() == FineStatus.OPEN).count());
		dto.setTotalFineAmountCents(fines.stream().mapToLong(Fine::getAmountCents).sum());
		dto.setUsersWithDebt(fines.stream().filter(fine -> fine.getStatus() == FineStatus.OPEN).map(Fine::getUserUid).distinct().count());
		return dto;
	}

	@Override
	public List<AdminTopBookDto> topBorrowedBooks(int limit) {
		Map<Long, Long> counts = new HashMap<>();
		for (Loan loan : loanRepository.findAll()) {
			counts.merge(loan.getBookId(), 1L, Long::sum);
		}
		return counts.entrySet().stream()
				.sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
				.limit(Math.max(1, limit))
				.map(entry -> buildBookDto(bookRepository.findById(entry.getKey()).orElse(null), entry.getValue(), 0))
				.toList();
	}

	@Override
	public List<AdminTopBookDto> topFavoritedBooks(int limit) {
		Map<Long, Long> counts = new HashMap<>();
		for (Object[] row : favoriteBookRepository.countByBookId()) {
			Long bookId = ((Number) row[0]).longValue();
			Long count = ((Number) row[1]).longValue();
			counts.put(bookId, count);
		}
		return bookRepository.findAll().stream()
				.map(book -> buildBookDto(book, 0, counts.getOrDefault(book.getId(), 0L)))
				.sorted(Comparator.comparingLong(AdminTopBookDto::getFavoriteCount).reversed())
				.limit(Math.max(1, limit))
				.toList();
	}

	@Override
	public List<AdminTopUserDto> topUsers(int limit) {
		Map<String, Long> counts = new HashMap<>();
		for (Loan loan : loanRepository.findAll()) {
			counts.merge(loan.getUserUid(), 1L, Long::sum);
		}
		return counts.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.limit(Math.max(1, limit))
				.map(entry -> {
					var user = userRepository.findByUid(entry.getKey()).orElse(null);
					AdminTopUserDto dto = new AdminTopUserDto();
					dto.setUserUid(entry.getKey());
					dto.setDisplayName(user != null ? user.getDisplayName() : null);
					dto.setEmail(user != null ? user.getEmail() : null);
					dto.setTotalLoans(entry.getValue());
					dto.setActiveLoans(loanRepository.findByUser_UidOrderByBorrowedAtDesc(entry.getKey()).stream()
							.filter(loan -> loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE)
							.count());
					return dto;
				})
				.toList();
	}

	@Override
	public List<AdminMonthlySeriesPointDto> monthlyActivity(int months) {
		Map<String, AdminMonthlySeriesPointDto> points = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);
		for (Object[] row : loanRepository.monthlyLoanStats()) {
			AdminMonthlySeriesPointDto point = new AdminMonthlySeriesPointDto();
			point.setMonth(String.valueOf(row[0]));
			point.setLoans(((Number) row[1]).longValue());
			point.setSignups(0);
			point.setReservations(0);
			point.setFines(0);
			points.put(point.getMonth(), point);
		}
		for (var user : userRepository.findAll()) {
			if (user.getCreatedAt() != null) {
				points.computeIfAbsent(formatter.format(user.getCreatedAt()), this::newPoint).setSignups(
						points.get(formatter.format(user.getCreatedAt())).getSignups() + 1);
			}
		}
		for (Reservation reservation : reservationRepository.findAll()) {
			if (reservation.getCreatedAt() != null) {
				points.computeIfAbsent(formatter.format(reservation.getCreatedAt()), this::newPoint).setReservations(
						points.get(formatter.format(reservation.getCreatedAt())).getReservations() + 1);
			}
		}
		for (Fine fine : fineRepository.findAll()) {
			if (fine.getCreatedAt() != null) {
				points.computeIfAbsent(formatter.format(fine.getCreatedAt()), this::newPoint).setFines(
						points.get(formatter.format(fine.getCreatedAt())).getFines() + 1);
			}
		}
		return points.values().stream()
				.sorted(Comparator.comparing(AdminMonthlySeriesPointDto::getMonth).reversed())
				.limit(Math.max(1, months))
				.toList();
	}

	@Override
	public List<AdminCategoryCountDto> booksByCategory(int limit) {
		Map<Long, Long> counts = new HashMap<>();
		for (Book book : bookRepository.findAll()) {
			counts.merge(book.getCategoryId(), 1L, Long::sum);
		}
		return counts.entrySet().stream()
				.sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
				.limit(Math.max(1, limit))
				.map(entry -> {
					AdminCategoryCountDto dto = new AdminCategoryCountDto();
					dto.setCategoryId(entry.getKey());
					dto.setCategoryName(bookRepository.findAll().stream()
							.filter(book -> entry.getKey() != null && entry.getKey().equals(book.getCategoryId()))
							.map(book -> book.getCategory() != null ? book.getCategory().getName() : "Uncategorized")
							.findFirst()
							.orElse("Uncategorized"));
					dto.setBooks(entry.getValue());
					return dto;
				})
				.toList();
	}

	private AdminTopBookDto buildBookDto(Book book, long borrowCount, long favoriteCount) {
		AdminTopBookDto dto = new AdminTopBookDto();
		if (book != null) {
			dto.setBookId(book.getId());
			dto.setTitle(book.getTitle());
			dto.setAuthor(book.getAuthor());
			dto.setCoverUrl(book.getCoverUrl());
			dto.setTotalCopies(book.getTotalCopies());
			dto.setAvailableCopies(book.getAvailableCopies());
		}
		dto.setBorrowCount(borrowCount);
		dto.setFavoriteCount(favoriteCount);
		return dto;
	}

	private AdminMonthlySeriesPointDto newPoint(String month) {
		AdminMonthlySeriesPointDto point = new AdminMonthlySeriesPointDto();
		point.setMonth(month);
		point.setLoans(0);
		point.setSignups(0);
		point.setReservations(0);
		point.setFines(0);
		return point;
	}
}
