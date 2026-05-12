package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bookcloud.smartlibrary.enums.NewsType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	private NewsType type;

	private String imageUrl;

	private boolean active;

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant updatedAt;
}
