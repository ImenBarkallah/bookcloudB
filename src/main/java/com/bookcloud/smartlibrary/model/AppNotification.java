package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.bookcloud.smartlibrary.enums.NotificationChannel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_notifications")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AppNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private AppUser user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationChannel channel = NotificationChannel.IN_APP;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "is_read")
	private boolean read;

	public String getUserUid() {
		return user != null ? user.getUid() : null;
	}
}
