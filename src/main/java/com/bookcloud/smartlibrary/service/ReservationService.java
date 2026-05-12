package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.AdminReservationDetailDto;
import com.bookcloud.smartlibrary.dto.AdminReservationListItemDto;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.Reservation;

public interface ReservationService {
	Reservation create(Long bookId, String userUid, Long pickupBranchId);
	Reservation cancel(Long reservationId, String actingUid, Role callerRole);
	Reservation approve(Long reservationId, Role callerRole);
	Reservation reject(Long reservationId, Role callerRole);
	Long convertToLoan(Long reservationId, Role callerRole);
	Reservation complete(Long reservationId, Role callerRole);
	List<Reservation> listForUser(String userUid);
	List<Reservation> listAll();
	List<AdminReservationListItemDto> listAllForAdmin();
	AdminReservationDetailDto getAdminDetail(Long reservationId, String actingUid, Role role);
	Reservation getByIdForCaller(Long id, String actingUid, Role role);
}
