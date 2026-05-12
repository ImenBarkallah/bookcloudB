package com.bookcloud.smartlibrary.service;

import com.bookcloud.smartlibrary.dto.KeycloakTokenResponse;
import com.bookcloud.smartlibrary.enums.Role;

public interface KeycloakIdentityService {

	String registerUser(String email, String password, String displayName, String firstName, String lastName);

	String registerUser(String email, String password, String displayName, String firstName, String lastName, Role role);

	KeycloakTokenResponse loginWithPassword(String email, String password);

	void sendPasswordResetEmail(String email);

	void changePassword(String userId, String usernameOrEmail, String currentPassword, String newPassword);

	void deleteUser(String userId);

	void updateUserRole(String userId, Role role);
}
