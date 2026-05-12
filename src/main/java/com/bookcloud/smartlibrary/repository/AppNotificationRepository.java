package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.model.AppNotification;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

	@EntityGraph(attributePaths = "user")
	List<AppNotification> findByUser_UidOrderByCreatedAtDesc(String uid);
}
