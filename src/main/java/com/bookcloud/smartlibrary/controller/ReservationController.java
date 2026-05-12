package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.AdminReservationDetailDto;
import com.bookcloud.smartlibrary.dto.AdminReservationListItemDto;
import com.bookcloud.smartlibrary.dto.CreateReservationRequest;
import com.bookcloud.smartlibrary.model.Reservation;
import com.bookcloud.smartlibrary.service.ReservationService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<Reservation> myReservations(Authentication authentication) {
		return reservationService.listForUser(SecurityUtils.uid(authentication));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminReservationListItemDto> listAll() {
		return reservationService.listAllForAdmin();
	}

	@GetMapping("/{id}/admin-detail")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public AdminReservationDetailDto adminDetail(@PathVariable Long id, Authentication authentication) {
		return reservationService.getAdminDetail(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Reservation get(@PathVariable Long id, Authentication authentication) {
		return reservationService.getByIdForCaller(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Reservation create(@Valid @RequestBody CreateReservationRequest req, Authentication authentication) {
		return reservationService.create(req.getBookId(), SecurityUtils.uid(authentication),
				req.getPickupBranchId());
	}

	@PostMapping("/{id}/cancel")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Reservation cancel(@PathVariable Long id, Authentication authentication) {
		return reservationService.cancel(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@PostMapping("/{id}/complete")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public Reservation complete(@PathVariable Long id, Authentication authentication) {
		return reservationService.complete(id, SecurityUtils.currentRole(authentication));
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public Reservation approve(@PathVariable Long id, Authentication authentication) {
		return reservationService.approve(id, SecurityUtils.currentRole(authentication));
	}

	@PostMapping("/{id}/reject")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public Reservation reject(@PathVariable Long id, Authentication authentication) {
		return reservationService.reject(id, SecurityUtils.currentRole(authentication));
	}

	@PostMapping("/{id}/convert-to-loan")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public Long convertToLoan(@PathVariable Long id, Authentication authentication) {
		return reservationService.convertToLoan(id, SecurityUtils.currentRole(authentication));
	}
}
