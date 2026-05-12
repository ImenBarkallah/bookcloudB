package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.AdminCategoryCountDto;
import com.bookcloud.smartlibrary.dto.AdminDashboardOverviewDto;
import com.bookcloud.smartlibrary.dto.AdminMonthlySeriesPointDto;
import com.bookcloud.smartlibrary.dto.AdminTopBookDto;
import com.bookcloud.smartlibrary.dto.AdminTopUserDto;
import com.bookcloud.smartlibrary.model.LibraryHistoryEntry;
import com.bookcloud.smartlibrary.repository.LibraryHistoryRepository;
import com.bookcloud.smartlibrary.service.AdminDashboardStatsService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardStatsController {

	private final AdminDashboardStatsService stats;
	private final LibraryHistoryRepository history;

	public AdminDashboardStatsController(AdminDashboardStatsService stats, LibraryHistoryRepository history) {
		this.stats = stats;
		this.history = history;
	}

	@GetMapping("/overview")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public AdminDashboardOverviewDto overview() {
		return stats.overview();
	}

	@GetMapping("/top-books/borrowed")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminTopBookDto> topBorrowed(@RequestParam(defaultValue = "5") int limit) {
		return stats.topBorrowedBooks(limit);
	}

	@GetMapping("/top-books/favorited")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminTopBookDto> topFavorited(@RequestParam(defaultValue = "5") int limit) {
		return stats.topFavoritedBooks(limit);
	}

	@GetMapping("/top-users")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminTopUserDto> topUsers(@RequestParam(defaultValue = "5") int limit) {
		return stats.topUsers(limit);
	}

	@GetMapping("/monthly")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminMonthlySeriesPointDto> monthly(@RequestParam(defaultValue = "12") int months) {
		return stats.monthlyActivity(months);
	}

	@GetMapping("/books-by-category")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminCategoryCountDto> booksByCategory(@RequestParam(defaultValue = "8") int limit) {
		return stats.booksByCategory(limit);
	}

	@GetMapping("/recent-activity")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<LibraryHistoryEntry> recent(@RequestParam(defaultValue = "12") int limit) {
		return history.findRecent(PageRequest.of(0, limit));
	}
}
