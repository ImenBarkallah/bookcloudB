package com.bookcloud.smartlibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, KeycloakJwtConverter keycloakJwtConverter)
			throws Exception {
		JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
		authenticationConverter.setJwtGrantedAuthoritiesConverter(keycloakJwtConverter);

		http.csrf(csrf -> csrf.disable());
		http.cors(Customizer.withDefaults());
		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.httpBasic(basic -> basic.disable());
		http.formLogin(form -> form.disable());
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/public/auth/**").permitAll()
				// Allow all GET requests; admin/protected GET endpoints are guarded by @PreAuthorize.
				.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
				.anyRequest().authenticated());
		http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)));
		http.exceptionHandling(ex -> ex
				.authenticationEntryPoint((req, res, e) -> res.sendError(401))
				.accessDeniedHandler((req, res, e) -> res.sendError(403)));
		return http.build();
	}
}
