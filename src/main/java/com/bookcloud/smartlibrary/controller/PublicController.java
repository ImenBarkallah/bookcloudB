package com.bookcloud.smartlibrary.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.PublicHomeStatsDto;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;

@RestController
@RequestMapping("/api/public")
public class PublicController {

	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	public PublicController(BookRepository bookRepository, UserRepository userRepository) {
		this.bookRepository = bookRepository;
		this.userRepository = userRepository;
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("status", "UP");
	}

	@GetMapping("/home-stats")
	public PublicHomeStatsDto homeStats() {
		return new PublicHomeStatsDto(bookRepository.count(), userRepository.count());
	}
}
