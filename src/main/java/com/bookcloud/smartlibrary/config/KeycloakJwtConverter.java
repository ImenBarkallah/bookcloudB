package com.bookcloud.smartlibrary.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeycloakJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Object realmAccess = jwt.getClaim("realm_access");
		if (!(realmAccess instanceof Map<?, ?> accessMap)) {
			return Collections.emptySet();
		}
		Object roles = accessMap.get("roles");
		if (!(roles instanceof List<?> roleList)) {
			return Collections.emptySet();
		}
		Set<String> allowed = Set.of("ADMIN", "LIBRARIAN", "USER");
		return roleList.stream()
				.map(String::valueOf)
				.map(String::trim)
				.map(String::toUpperCase)
				.filter(allowed::contains)
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toSet());
	}
}
