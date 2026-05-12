package com.bookcloud.smartlibrary.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.config.AppProperties;
import com.bookcloud.smartlibrary.dto.UpdateLibrarySettingsRequest;
import com.bookcloud.smartlibrary.model.LibrarySettings;
import com.bookcloud.smartlibrary.repository.LibrarySettingsRepository;
import com.bookcloud.smartlibrary.service.LibrarySettingsService;

@Service
@Transactional
public class LibrarySettingsServiceImpl implements LibrarySettingsService {

	private final LibrarySettingsRepository repository;
	private final AppProperties appProperties;

	public LibrarySettingsServiceImpl(LibrarySettingsRepository repository, AppProperties appProperties) {
		this.repository = repository;
		this.appProperties = appProperties;
	}

	@Override
	public LibrarySettings getOrDefault() {
		return repository.findTopByOrderByIdAsc().orElseGet(() -> {
			LibrarySettings settings = new LibrarySettings();
			settings.setDefaultLoanDays(appProperties.getLoanDefaultDays());
			settings.setReservationExpiryDays(appProperties.getReservationExpiryDays());
			settings.setMaxRenewalsPerLoan(appProperties.getMaxRenewalsPerLoan());
			settings.setOverdueFinePerDayCents(appProperties.getOverdueFinePerDayCents());
			settings.setMaxActiveLoansDefault(5);
			settings.setFinePerDay(0D);
			return repository.save(settings);
		});
	}

	@Override
	public LibrarySettings update(UpdateLibrarySettingsRequest req) {
		LibrarySettings settings = getOrDefault();
		if (req.getDefaultLoanDays() != null) {
			settings.setDefaultLoanDays(req.getDefaultLoanDays());
		}
		if (req.getMaxActiveLoansDefault() != null) {
			settings.setMaxActiveLoansDefault(req.getMaxActiveLoansDefault());
		}
		if (req.getReservationExpiryDays() != null) {
			settings.setReservationExpiryDays(req.getReservationExpiryDays());
		}
		if (req.getFinePerDay() != null) {
			settings.setFinePerDay(req.getFinePerDay());
		}
		return repository.save(settings);
	}
}
