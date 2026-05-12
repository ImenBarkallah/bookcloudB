package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.BookCatalogItemDto;
import com.bookcloud.smartlibrary.service.BookService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

	private final BookService bookService;

	public DiscoveryController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/popular")
	@PreAuthorize("permitAll()")
	public List<BookCatalogItemDto> popular(@RequestParam(defaultValue = "12") int limit,
			Authentication authentication) {
		String uid = authentication != null && authentication.isAuthenticated() ? SecurityUtils.uid(authentication)
				: null;
		return bookService.discoveryPopular(limit, uid);
	}

	@GetMapping("/recommendations")
	@PreAuthorize("isAuthenticated()")
	public List<BookCatalogItemDto> recommendations(@RequestParam(defaultValue = "12") int limit,
			Authentication authentication) {
		return bookService.discoveryRecommendations(SecurityUtils.uid(authentication), limit);
	}
}
