package com.bookcloud.smartlibrary.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.bookcloud.smartlibrary.enums.Role;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static String uid(Authentication authentication) {
		return authentication != null ? authentication.getName() : "";
	}

	public static Role currentRole(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return Role.USER;
		}
		Optional<String> authority = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(value -> value.startsWith("ROLE_"))
				.map(value -> value.substring("ROLE_".length()))
				.findFirst();
		if (authority.isEmpty()) {
			return Role.USER;
		}
		try {
			return Role.valueOf(authority.get());
		} catch (IllegalArgumentException ex) {
			return Role.USER;
		}
	}
}
