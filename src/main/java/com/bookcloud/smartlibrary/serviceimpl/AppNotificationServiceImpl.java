package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.CreateAppNotificationRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.AppNotification;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.repository.AppNotificationRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.AppNotificationService;

@Service
@Transactional
public class AppNotificationServiceImpl implements AppNotificationService {

	private final AppNotificationRepository appNotificationRepository;
	private final UserRepository userRepository;

	public AppNotificationServiceImpl(AppNotificationRepository appNotificationRepository, UserRepository userRepository) {
		this.appNotificationRepository = appNotificationRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppNotification> listMine(String userUid) {
		return appNotificationRepository.findByUser_UidOrderByCreatedAtDesc(userUid);
	}

	@Override
	public AppNotification createForUser(CreateAppNotificationRequest req, Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new BusinessRuleException("Staff access required");
		}
		AppUser user = userRepository.findByUid(req.getUserUid())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		AppNotification notification = new AppNotification();
		notification.setUser(user);
		notification.setChannel(req.getChannel());
		notification.setMessage(req.getMessage());
		return appNotificationRepository.save(notification);
	}

	@Override
	public AppNotification markRead(Long id, boolean read, String actingUid, Role role) {
		AppNotification notification = appNotificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
		if (!actingUid.equals(notification.getUserUid()) && role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new BusinessRuleException("Notification access denied");
		}
		notification.setRead(read);
		return appNotificationRepository.save(notification);
	}
}
