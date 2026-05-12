package com.bookcloud.smartlibrary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.KeycloakIdentityService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final KeycloakIdentityService keycloakIdentityService;
	private final UserRepository userRepository;

	public AuthController(KeycloakIdentityService keycloakIdentityService, UserRepository userRepository) {
		this.keycloakIdentityService = keycloakIdentityService;
		this.userRepository = userRepository;
	}

	public static record ChangePasswordRequest(String currentPassword, String newPassword) {
	}

	@PostMapping("/change-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(Authentication authentication, @RequestBody ChangePasswordRequest req) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		String userId = SecurityUtils.uid(authentication);
		String usernameOrEmail = resolveEmailOrUsername(authentication);
		keycloakIdentityService.changePassword(userId, usernameOrEmail, req.currentPassword(), req.newPassword());
	}

	@DeleteMapping("/me")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCurrentUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}

		String userId = SecurityUtils.uid(authentication);
		keycloakIdentityService.deleteUser(userId);

		userRepository.findByUid(userId).ifPresent(user -> {
			user.setBlocked(true);
			userRepository.save(user);
		});
	}

	private String resolveEmailOrUsername(Authentication authentication) {
		Jwt jwt = extractJwt(authentication);
		if (jwt == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT not found");
		}
		String email = jwt.getClaimAsString("email");
		if (StringUtils.hasText(email)) {
			return email.trim();
		}
		String preferredUsername = jwt.getClaimAsString("preferred_username");
		if (StringUtils.hasText(preferredUsername)) {
			return preferredUsername.trim();
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username not found");
	}

	private Jwt extractJwt(Authentication authentication) {
		if (authentication instanceof JwtAuthenticationToken token) {
			return token.getToken();
		}
		Object principal = authentication != null ? authentication.getPrincipal() : null;
		return principal instanceof Jwt jwt ? jwt : null;
	}
}
