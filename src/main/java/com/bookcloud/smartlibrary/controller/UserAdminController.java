package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.AdminUserRowDto;
import com.bookcloud.smartlibrary.dto.CreateAdminUserRequest;
import com.bookcloud.smartlibrary.dto.UpdateAdminUserRequest;
import com.bookcloud.smartlibrary.dto.UpdateUserRoleRequest;
import com.bookcloud.smartlibrary.service.UserAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserAdminController {

	private final UserAdminService userAdminService;

	public UserAdminController(UserAdminService userAdminService) {
		this.userAdminService = userAdminService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminUserRowDto> list() {
		return userAdminService.listAllRows();
	}

	@GetMapping("/{uid}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public AdminUserRowDto get(@PathVariable String uid) {
		return userAdminService.getRowById(uid);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public AdminUserRowDto create(@Valid @RequestBody CreateAdminUserRequest req) {
		return userAdminService.createUser(req);
	}

	@PutMapping("/{uid}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public AdminUserRowDto update(@PathVariable String uid, @Valid @RequestBody UpdateAdminUserRequest req) {
		return userAdminService.updateProfile(uid, req);
	}

	@PutMapping("/{uid}/role")
	@PreAuthorize("hasRole('ADMIN')")
	public AdminUserRowDto updateRole(@PathVariable String uid, @Valid @RequestBody UpdateUserRoleRequest req) {
		userAdminService.updateRole(uid, req.getRole());
		return userAdminService.getRowById(uid);
	}

	@PutMapping("/{uid}/blocked/{blocked}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public AdminUserRowDto setBlocked(@PathVariable String uid, @PathVariable boolean blocked) {
		return userAdminService.setBlocked(uid, blocked);
	}

	@DeleteMapping("/{uid}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable String uid) {
		userAdminService.deleteUser(uid);
		return ResponseEntity.noContent().build();
	}
}
