package com.bookcloud.smartlibrary.dto;

import com.bookcloud.smartlibrary.enums.Role;

/**
 * Ligne de table admin (gestion utilisateurs).
 * DTO séparé de {@link com.bookcloud.smartlibrary.model.AppUser} pour pouvoir
 * exposer des champs optionnels (firstName/lastName) sans impacter la sécurité.
 */
public class AdminUserRowDto {
	private String uid;
	private String email;
	private String displayName;
	private String firstName;
	private String lastName;
	private Role role;
	private boolean blocked;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}

