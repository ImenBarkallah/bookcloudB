package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateAppNotificationRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.AppNotification;

public interface AppNotificationService {
	List<AppNotification> listMine(String userUid);
	AppNotification createForUser(CreateAppNotificationRequest req, Role role);
	AppNotification markRead(Long id, boolean read, String actingUid, Role role);
}
