package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateAppNotificationRequest;
import com.bookcloud.smartlibrary.dto.PatchNotificationReadRequest;
import com.bookcloud.smartlibrary.model.AppNotification;
import com.bookcloud.smartlibrary.service.AppNotificationService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class AppNotificationController {

	private final AppNotificationService appNotificationService;

	public AppNotificationController(AppNotificationService appNotificationService) {
		this.appNotificationService = appNotificationService;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<AppNotification> mine(Authentication authentication) {
		return appNotificationService.listMine(SecurityUtils.uid(authentication));
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public AppNotification create(@Valid @RequestBody CreateAppNotificationRequest req,
			Authentication authentication) {
		return appNotificationService.createForUser(req, SecurityUtils.currentRole(authentication));
	}

	@PatchMapping("/{id}/read")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public AppNotification markRead(@PathVariable Long id, @RequestBody(required = false) PatchNotificationReadRequest body,
			Authentication authentication) {
		boolean read = body == null || body.isRead();
		return appNotificationService.markRead(id, read, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}
}
