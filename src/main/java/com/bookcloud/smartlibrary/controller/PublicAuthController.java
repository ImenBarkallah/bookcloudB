package com.bookcloud.smartlibrary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.KeycloakTokenResponse;
import com.bookcloud.smartlibrary.service.KeycloakIdentityService;

@RestController
@RequestMapping("/api/public/auth")
public class PublicAuthController {

	private final KeycloakIdentityService keycloakIdentityService;

	public PublicAuthController(KeycloakIdentityService keycloakIdentityService) {
		this.keycloakIdentityService = keycloakIdentityService;
	}

	public static record RegisterRequest(String email, String password, String displayName, String firstName,
			String lastName) {
	}

	public static record ForgotPasswordRequest(String email) {
	}

	public static record LoginRequest(String email, String password) {
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@RequestBody RegisterRequest req) {
		keycloakIdentityService.registerUser(req.email(), req.password(), req.displayName(), req.firstName(),
				req.lastName());
	}

	@PostMapping("/login")
	public KeycloakTokenResponse login(@RequestBody LoginRequest req) {
		return keycloakIdentityService.loginWithPassword(req.email(), req.password());
	}

	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@RequestBody ForgotPasswordRequest req) {
		keycloakIdentityService.sendPasswordResetEmail(req.email());
	}
}
