package com.bookcloud.smartlibrary.serviceimpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.UserProvisioningService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@Service
@Transactional
public class UserProvisioningServiceImpl implements UserProvisioningService {

	private final UserRepository userRepository;

	public UserProvisioningServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public AppUser ensureUser(Authentication authentication) {
		String uid = SecurityUtils.uid(authentication);
		Jwt jwt = extractJwt(authentication);
		String email = jwt != null ? jwt.getClaimAsString("email") : null;
		String displayName = resolveDisplayName(jwt);
		Role role = SecurityUtils.currentRole(authentication);

		AppUser user = userRepository.findByUid(uid).orElseGet(AppUser::new);
		if (user.getId() == null) {
			user.setUid(uid);
			user.setRole(role);
		}
		if (StringUtils.hasText(email)) {
			user.setEmail(email);
		}
		if (StringUtils.hasText(displayName)) {
			user.setDisplayName(displayName);
		}
		if (!StringUtils.hasText(user.getFirstName()) && jwt != null) {
			user.setFirstName(jwt.getClaimAsString("given_name"));
		}
		if (!StringUtils.hasText(user.getLastName()) && jwt != null) {
			user.setLastName(jwt.getClaimAsString("family_name"));
		}
		if (user.getRole() == null) {
			user.setRole(role);
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

	private String resolveDisplayName(Jwt jwt) {
		if (jwt == null) {
			return null;
		}
		String name = jwt.getClaimAsString("name");
		if (StringUtils.hasText(name)) {
			return name;
		}
		String givenName = jwt.getClaimAsString("given_name");
		String familyName = jwt.getClaimAsString("family_name");
		String joined = (givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "");
		if (StringUtils.hasText(joined.trim())) {
			return joined.trim();
		}
		return jwt.getClaimAsString("preferred_username");
	}
}
