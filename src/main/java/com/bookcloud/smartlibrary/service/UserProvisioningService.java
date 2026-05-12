package com.bookcloud.smartlibrary.service;

import org.springframework.security.core.Authentication;

import com.bookcloud.smartlibrary.model.AppUser;

public interface UserProvisioningService {
	AppUser ensureUser(Authentication authentication);
}
