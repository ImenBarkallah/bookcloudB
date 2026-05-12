package com.bookcloud.smartlibrary.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {

	private static final List<String> DEFAULT_ALLOWED_HEADERS = List.of(
			"Authorization",
			"Content-Type",
			"Accept",
			"Origin",
			"X-Requested-With",
			"ngrok-skip-browser-warning");

	@Bean
	CorsConfigurationSource corsConfigurationSource(AppProperties appProperties) {
		CorsConfiguration configuration = new CorsConfiguration();
		List<String> originPatterns = new ArrayList<>();
		originPatterns.add("http://localhost:*");
		originPatterns.add("http://127.0.0.1:*");
		originPatterns.add("https://*.ngrok-free.app");
		originPatterns.add("https://*.web.app");
		originPatterns.add("https://*.firebaseapp.com");
		originPatterns.add("https://bookcloud-83374.web.app");
		originPatterns.add("https://bookcloud-83374.firebaseapp.com");
		String configuredOrigin = appProperties.getFrontend().getOrigin();
		if (configuredOrigin != null && !configuredOrigin.isBlank()) {
			originPatterns.add(configuredOrigin.trim());
		}
		configuration.setAllowedOriginPatterns(originPatterns);
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(DEFAULT_ALLOWED_HEADERS);
		configuration.setExposedHeaders(List.of("Authorization"));
		configuration.setAllowCredentials(false);
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
