package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.AdminUserRowDto;
import com.bookcloud.smartlibrary.dto.CreateAdminUserRequest;
import com.bookcloud.smartlibrary.dto.UpdateAdminUserRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.AppUser;

public interface UserAdminService {
	List<AppUser> listAll();
	List<AdminUserRowDto> listAllRows();
	AppUser getById(String uid);
	AdminUserRowDto getRowById(String uid);
	AdminUserRowDto createUser(CreateAdminUserRequest req);
	AdminUserRowDto updateProfile(String uid, UpdateAdminUserRequest req);
	AppUser updateRole(String uid, Role newRole);
	AdminUserRowDto setBlocked(String uid, boolean blocked);
	void deleteUser(String uid);
}
