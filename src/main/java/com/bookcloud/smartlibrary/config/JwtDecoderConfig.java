package com.bookcloud.smartlibrary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

	@Bean
	JwtDecoder jwtDecoder(
			@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
			@Value("${app.security.expected-issuer:http://localhost:8180/realms/bookcloud}") String expectedIssuer) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(expectedIssuer));
		return decoder;
	}
}

