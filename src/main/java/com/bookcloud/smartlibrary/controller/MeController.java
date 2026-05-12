package com.bookcloud.smartlibrary.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.UserProvisioningService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@RestController
@RequestMapping("/api")
public class MeController {

	private final UserProvisioningService userProvisioningService;
	private final UserRepository userRepository;

	public MeController(UserProvisioningService userProvisioningService, UserRepository userRepository) {
		this.userProvisioningService = userProvisioningService;
		this.userRepository = userRepository;
	}

	@GetMapping("/me")
	public Map<String, Object> me(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return Map.of("uid", "", "email", "", "role", "");
		}

		Jwt jwt = extractJwt(authentication);
		String uid = SecurityUtils.uid(authentication);
		String email = jwt != null && jwt.getClaimAsString("email") != null ? jwt.getClaimAsString("email") : "";
		Role role = SecurityUtils.currentRole(authentication);
		return Map.of("uid", uid, "email", email, "role", role.name());
	}

	@PostMapping("/me/provision")
	public AppUser provision(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		return userProvisioningService.ensureUser(authentication);
	}

	public static record UpdateMeProfileRequest(String firstName, String lastName, String phone) {
	}

	@PutMapping("/me/profile")
	public AppUser updateProfile(Authentication authentication, @RequestBody UpdateMeProfileRequest req) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		String uid = SecurityUtils.uid(authentication);
		AppUser user = userRepository.findByUid(uid)
				.orElseGet(() -> userProvisioningService.ensureUser(authentication));

		String firstName = req != null ? req.firstName() : null;
		String lastName = req != null ? req.lastName() : null;
		String phone = req != null ? req.phone() : null;

		if (firstName != null) {
			user.setFirstName(firstName.trim());
		}
		if (lastName != null) {
			user.setLastName(lastName.trim());
		}
		if (phone != null) {
			user.setPhone(phone.trim());
		}

		String dn = ((StringUtils.hasText(user.getFirstName()) ? user.getFirstName() : "") + " "
				+ (StringUtils.hasText(user.getLastName()) ? user.getLastName() : "")).trim();
		if (StringUtils.hasText(dn)) {
			user.setDisplayName(dn);
		}

		return userRepository.save(user);
	}

	private Jwt extractJwt(Authentication authentication) {
		if (authentication instanceof JwtAuthenticationToken token) {
			return token.getToken();
		}
		Object principal = authentication != null ? authentication.getPrincipal() : null;
		return principal instanceof Jwt jwt ? jwt : null;
	}
}
