package com.bookcloud.smartlibrary.serviceimpl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.bookcloud.smartlibrary.dto.KeycloakTokenResponse;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.service.KeycloakIdentityService;
import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class KeycloakIdentityServiceImpl implements KeycloakIdentityService {

	private final RestClient restClient;
	private final String keycloakBaseUrl;
	private final String realm;
	private final String clientId;
	private final String adminRealm;
	private final String adminClientId;
	private final String adminUsername;
	private final String adminPassword;
	private final String frontendRedirectUri;

	public KeycloakIdentityServiceImpl(
			@Value("${app.keycloak.base-url:http://localhost:8180}") String keycloakBaseUrl,
			@Value("${app.keycloak.realm:bookcloud}") String realm,
			@Value("${app.keycloak.client-id:bookcloud-frontend}") String clientId,
			@Value("${app.keycloak.admin-realm:master}") String adminRealm,
			@Value("${app.keycloak.admin-client-id:admin-cli}") String adminClientId,
			@Value("${app.keycloak.admin-username:admin}") String adminUsername,
			@Value("${app.keycloak.admin-password:admin}") String adminPassword,
			@Value("${app.keycloak.frontend-redirect-uri:http://localhost:4200/home}") String frontendRedirectUri) {
		this.restClient = RestClient.builder().build();
		this.keycloakBaseUrl = keycloakBaseUrl;
		this.realm = realm;
		this.clientId = clientId;
		this.adminRealm = adminRealm;
		this.adminClientId = adminClientId;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
		this.frontendRedirectUri = frontendRedirectUri;
	}

	@Override
	public String registerUser(String email, String password, String displayName, String firstName, String lastName) {
		return registerUser(email, password, displayName, firstName, lastName, Role.USER);
	}

	@Override
	public String registerUser(String email, String password, String displayName, String firstName, String lastName,
			Role role) {
		String normalizedEmail = normalizeEmail(email);
		String normalizedPassword = normalizeRequired(password, "Password is required");
		String normalizedDisplayName = StringUtils.hasText(displayName) ? displayName.trim() : normalizedEmail;
		String givenName = StringUtils.hasText(firstName) ? firstName.trim() : firstToken(normalizedDisplayName);
		String familyName = StringUtils.hasText(lastName) ? lastName.trim() : remainingTokens(normalizedDisplayName);

		String adminToken = adminAccessToken();

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("username", normalizedEmail);
		payload.put("email", normalizedEmail);
		payload.put("enabled", true);
		payload.put("emailVerified", true);
		payload.put("firstName", givenName);
		payload.put("lastName", familyName);
		payload.put("credentials", List.of(Map.of(
				"type", "password",
				"value", normalizedPassword,
				"temporary", false)));

		try {
			var response = restClient.post()
					.uri(adminUsersUri())
					.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
					.body(payload)
					.retrieve()
					.toBodilessEntity();

			String userId = extractUserId(response.getHeaders().getLocation());
			if (!StringUtils.hasText(userId)) {
				userId = findUserIdByEmail(normalizedEmail, adminToken);
			}
			if (!StringUtils.hasText(userId)) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak user creation failed");
			}

			resetPassword(adminToken, userId, normalizedPassword);
			syncRealmRole(adminToken, userId, role == null ? Role.USER : role);
			finalizeUserForLogin(adminToken, userId);
			return userId;
		} catch (HttpClientErrorException.Conflict ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
		} catch (HttpClientErrorException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
					"Keycloak registration failed: " + keycloakErrorMessage(ex));
		}
	}

	@Override
	public KeycloakTokenResponse loginWithPassword(String email, String password) {
		return requestPasswordGrant(normalizeEmail(email), normalizeRequired(password, "Password is required"));
	}

	@Override
	public void sendPasswordResetEmail(String email) {
		String normalizedEmail = normalizeEmail(email);
		String adminToken = adminAccessToken();
		String userId = findUserIdByEmail(normalizedEmail, adminToken);
		if (!StringUtils.hasText(userId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}

		restClient.put()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId
						+ "/execute-actions-email?client_id=" + clientId + "&redirect_uri=" + frontendRedirectUri)
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.body(List.of("UPDATE_PASSWORD"))
				.retrieve()
				.toBodilessEntity();
	}

	@Override
	public void changePassword(String userId, String usernameOrEmail, String currentPassword, String newPassword) {
		String normalizedUserId = normalizeRequired(userId, "User id is required");
		String normalizedUsername = normalizeEmail(usernameOrEmail);
		String normalizedCurrentPassword = normalizeRequired(currentPassword, "Current password is required");
		String normalizedNewPassword = normalizeRequired(newPassword, "New password is required");

		if (!verifyUserCredentials(normalizedUsername, normalizedCurrentPassword)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password");
		}

		String adminToken = adminAccessToken();
		resetPassword(adminToken, normalizedUserId, normalizedNewPassword);
	}

	@Override
	public void deleteUser(String userId) {
		String normalizedUserId = normalizeRequired(userId, "User id is required");
		String adminToken = adminAccessToken();
		restClient.delete()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + normalizedUserId)
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.retrieve()
				.toBodilessEntity();
	}

	@Override
	public void updateUserRole(String userId, Role role) {
		String normalizedUserId = normalizeRequired(userId, "User id is required");
		String adminToken = adminAccessToken();
		syncRealmRole(adminToken, normalizedUserId, role == null ? Role.USER : role);
	}

	private String adminAccessToken() {
		LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "password");
		form.add("client_id", adminClientId);
		form.add("username", adminUsername);
		form.add("password", adminPassword);

		TokenResponse token;
		try {
			token = restClient.post()
					.uri(keycloakBaseUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token")
					.header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
					.body(form)
					.retrieve()
					.body(TokenResponse.class);
		} catch (HttpClientErrorException.Unauthorized ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Keycloak admin authentication failed. Verify KEYCLOAK_ADMIN and KC_ADMIN_PASS.");
		} catch (HttpClientErrorException.BadRequest ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Keycloak admin-cli direct grant failed: " + keycloakErrorMessage(ex));
		} catch (RestClientResponseException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
					"Cannot get Keycloak admin token: " + keycloakErrorMessage(ex));
		}

		if (token == null || !StringUtils.hasText(token.accessToken())) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot get Keycloak admin token");
		}
		return token.accessToken();
	}

	private boolean verifyUserCredentials(String usernameOrEmail, String password) {
		try {
			requestPasswordGrant(usernameOrEmail, password);
			return true;
		} catch (ResponseStatusException ex) {
			return false;
		}
	}

	private KeycloakTokenResponse requestPasswordGrant(String usernameOrEmail, String password) {
		LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "password");
		form.add("client_id", clientId);
		form.add("username", usernameOrEmail);
		form.add("password", password);
		form.add("scope", "openid profile email");

		try {
			KeycloakTokenResponse token = restClient.post()
					.uri(keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token")
					.header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
					.body(form)
					.retrieve()
					.body(KeycloakTokenResponse.class);
			if (token == null || !StringUtils.hasText(token.getAccessToken())) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak login returned an empty token");
			}
			return token;
		} catch (HttpClientErrorException.Unauthorized ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		} catch (HttpClientErrorException.BadRequest ex) {
			String message = keycloakErrorMessage(ex);
			if (message.toLowerCase(Locale.ROOT).contains("invalid_grant")
					|| message.toLowerCase(Locale.ROOT).contains("invalid user credentials")) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
			}
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak login failed: " + message);
		} catch (HttpClientErrorException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak login failed: " + keycloakErrorMessage(ex));
		}
	}

	private void resetPassword(String adminToken, String userId, String newPassword) {
		Map<String, Object> credential = Map.of(
				"type", "password",
				"value", newPassword,
				"temporary", false);

		restClient.put()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password")
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.body(credential)
				.retrieve()
				.toBodilessEntity();
	}

	private void assignRealmRole(String adminToken, String userId, String roleName) {
		Map<String, Object> role = restClient.get()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName)
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		if (role == null || role.isEmpty()) {
			return;
		}

		restClient.post()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.body(List.of(role))
				.retrieve()
				.toBodilessEntity();
	}

	private void syncRealmRole(String adminToken, String userId, Role role) {
		removeManagedRealmRoles(adminToken, userId);
		assignRealmRole(adminToken, userId, role.name());
	}

	private void removeManagedRealmRoles(String adminToken, String userId) {
		List<Map<String, Object>> currentRoles = restClient.get()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.retrieve()
				.body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});

		if (currentRoles == null || currentRoles.isEmpty()) {
			return;
		}

		List<Map<String, Object>> managedRoles = currentRoles.stream()
				.filter(role -> {
					Object name = role.get("name");
					if (name == null) {
						return false;
					}
					String normalized = String.valueOf(name).trim().toUpperCase();
					return "ADMIN".equals(normalized) || "LIBRARIAN".equals(normalized) || "USER".equals(normalized);
				})
				.toList();

		if (managedRoles.isEmpty()) {
			return;
		}

		restClient.method(HttpMethod.DELETE)
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.body(managedRoles)
				.retrieve()
				.toBodilessEntity();
	}

	private String findUserIdByEmail(String email, String adminToken) {
		List<Map<String, Object>> users = restClient.get()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email + "&exact=true")
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.retrieve()
				.body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});

		if (users == null || users.isEmpty()) {
			return null;
		}

		Object id = users.get(0).get("id");
		return id != null ? String.valueOf(id) : null;
	}

	private URI adminUsersUri() {
		return URI.create(keycloakBaseUrl + "/admin/realms/" + realm + "/users");
	}

	private String extractUserId(URI location) {
		if (location == null) {
			return null;
		}
		String path = location.getPath();
		int idx = path.lastIndexOf('/');
		return idx >= 0 ? path.substring(idx + 1) : null;
	}

	private void finalizeUserForLogin(String adminToken, String userId) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("enabled", true);
		payload.put("emailVerified", true);
		payload.put("requiredActions", List.of());

		restClient.put()
				.uri(keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId)
				.header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
				.body(payload)
				.retrieve()
				.toBodilessEntity();
	}

	private String normalizeEmail(String email) {
		return normalizeRequired(email, "Email is required").toLowerCase(Locale.ROOT);
	}

	private String normalizeRequired(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
		}
		return value.trim();
	}

	private String firstToken(String displayName) {
		String[] parts = displayName.trim().split("\\s+");
		return parts.length > 0 ? parts[0] : "";
	}

	private String remainingTokens(String displayName) {
		String[] parts = displayName.trim().split("\\s+");
		if (parts.length <= 1) {
			return "";
		}
		return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}

	private String keycloakErrorMessage(RestClientResponseException ex) {
		String body = ex.getResponseBodyAsString();
		if (StringUtils.hasText(body)) {
			return body.trim();
		}
		String message = ex.getMessage();
		return StringUtils.hasText(message) ? message.trim() : "Unexpected Keycloak error";
	}

	private record TokenResponse(@JsonProperty("access_token") String accessToken) {
	}
}
