package com.bookcloud.smartlibrary.serviceimpl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bookcloud.smartlibrary.dto.AdminUserRowDto;
import com.bookcloud.smartlibrary.dto.CreateAdminUserRequest;
import com.bookcloud.smartlibrary.dto.UpdateAdminUserRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.KeycloakIdentityService;
import com.bookcloud.smartlibrary.service.UserAdminService;

@Service
@Transactional
public class UserAdminServiceImpl implements UserAdminService {

	private final UserRepository userRepository;
	private final KeycloakIdentityService keycloakIdentityService;

	public UserAdminServiceImpl(UserRepository userRepository, KeycloakIdentityService keycloakIdentityService) {
		this.userRepository = userRepository;
		this.keycloakIdentityService = keycloakIdentityService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppUser> listAll() {
		return userRepository.findAll().stream()
				.sorted(Comparator.comparing(user -> safe(user.getDisplayName())))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AdminUserRowDto> listAllRows() {
		return listAll().stream().map(this::toRow).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public AppUser getById(String uid) {
		return userRepository.findByUid(uid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public AdminUserRowDto getRowById(String uid) {
		return toRow(getById(uid));
	}

	@Override
	public AdminUserRowDto createUser(CreateAdminUserRequest req) {
		Role role = req.getRole() == null ? Role.USER : req.getRole();
		String uid = keycloakIdentityService.registerUser(
				req.getEmail(),
				req.getPassword(),
				effectiveDisplayName(req),
				trimToNull(req.getFirstName()),
				trimToNull(req.getLastName()),
				role);

		AppUser user = new AppUser();
		user.setUid(uid);
		user.setEmail(trimToNull(req.getEmail()));
		user.setDisplayName(effectiveDisplayName(req));
		user.setFirstName(trimToNull(req.getFirstName()));
		user.setLastName(trimToNull(req.getLastName()));
		user.setRole(role);
		user.setBlocked(false);
		return toRow(userRepository.save(user));
	}

	@Override
	public AdminUserRowDto updateProfile(String uid, UpdateAdminUserRequest req) {
		AppUser user = getById(uid);
		if (req.getFirstName() != null) {
			user.setFirstName(req.getFirstName());
		}
		if (req.getLastName() != null) {
			user.setLastName(req.getLastName());
		}
		if (req.getDisplayName() != null) {
			user.setDisplayName(req.getDisplayName());
		}
		if (req.getEmail() != null) {
			user.setEmail(req.getEmail());
		}
		return toRow(userRepository.save(user));
	}

	@Override
	public AppUser updateRole(String uid, Role newRole) {
		AppUser user = getById(uid);
		user.setRole(newRole);
		keycloakIdentityService.updateUserRole(uid, newRole);
		return userRepository.save(user);
	}

	@Override
	public AdminUserRowDto setBlocked(String uid, boolean blocked) {
		AppUser user = getById(uid);
		user.setBlocked(blocked);
		return toRow(userRepository.save(user));
	}

	@Override
	public void deleteUser(String uid) {
		userRepository.delete(getById(uid));
	}

	private AdminUserRowDto toRow(AppUser user) {
		AdminUserRowDto dto = new AdminUserRowDto();
		dto.setUid(user.getUid());
		dto.setEmail(user.getEmail());
		dto.setDisplayName(user.getDisplayName());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setRole(user.getRole());
		dto.setBlocked(user.isBlocked());
		return dto;
	}

	private String safe(String value) {
		return value == null ? "" : value.toLowerCase();
	}

	private String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private String effectiveDisplayName(CreateAdminUserRequest req) {
		String displayName = trimToNull(req.getDisplayName());
		if (displayName != null) {
			return displayName;
		}
		String firstName = trimToNull(req.getFirstName());
		String lastName = trimToNull(req.getLastName());
		String joined = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
		if (StringUtils.hasText(joined)) {
			return joined;
		}
		return trimToNull(req.getEmail());
	}
}
