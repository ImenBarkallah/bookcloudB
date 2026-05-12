package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.AdminCategoryCountDto;
import com.bookcloud.smartlibrary.dto.AdminDashboardOverviewDto;
import com.bookcloud.smartlibrary.dto.AdminMonthlySeriesPointDto;
import com.bookcloud.smartlibrary.dto.AdminTopBookDto;
import com.bookcloud.smartlibrary.dto.AdminTopUserDto;

public interface AdminDashboardStatsService {
	AdminDashboardOverviewDto overview();
	List<AdminTopBookDto> topBorrowedBooks(int limit);
	List<AdminTopBookDto> topFavoritedBooks(int limit);
	List<AdminTopUserDto> topUsers(int limit);
	List<AdminMonthlySeriesPointDto> monthlyActivity(int months);
	List<AdminCategoryCountDto> booksByCategory(int limit);
}
