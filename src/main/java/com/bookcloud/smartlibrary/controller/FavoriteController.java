package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.BookCatalogItemDto;
import com.bookcloud.smartlibrary.dto.FavoriteCountDto;
import com.bookcloud.smartlibrary.dto.FavoriteToggleRequest;
import com.bookcloud.smartlibrary.dto.FavoriteToggleResponse;
import com.bookcloud.smartlibrary.service.BookFavoriteService;
import com.bookcloud.smartlibrary.service.BookService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

	private final BookFavoriteService bookFavoriteService;
	private final BookService bookService;

	public FavoriteController(BookFavoriteService bookFavoriteService, BookService bookService) {
		this.bookFavoriteService = bookFavoriteService;
		this.bookService = bookService;
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<BookCatalogItemDto> list(@RequestParam(defaultValue = "200") int limit,
			Authentication authentication) {
		return bookService.listFavorites(SecurityUtils.uid(authentication), limit);
	}

	@GetMapping("/count")
	@PreAuthorize("isAuthenticated()")
	public FavoriteCountDto count(Authentication authentication) {
		int count = bookFavoriteService.countFavorites(SecurityUtils.uid(authentication));
		return new FavoriteCountDto(count);
	}

	@PostMapping("/toggle")
	@PreAuthorize("isAuthenticated()")
	public FavoriteToggleResponse toggle(@Valid @RequestBody FavoriteToggleRequest body,
			Authentication authentication) {
		boolean favorited = bookFavoriteService.toggle(SecurityUtils.uid(authentication), body.getBookId());
		return new FavoriteToggleResponse(body.getBookId(), favorited);
	}
}
