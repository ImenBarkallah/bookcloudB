package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.model.LibraryHistoryEntry;
import com.bookcloud.smartlibrary.repository.LibraryHistoryRepository;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@RestController
@RequestMapping("/api/history")
public class LibraryHistoryController {

	private final LibraryHistoryRepository libraryHistoryRepository;

	public LibraryHistoryController(LibraryHistoryRepository libraryHistoryRepository) {
		this.libraryHistoryRepository = libraryHistoryRepository;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<LibraryHistoryEntry> myHistory(Authentication authentication) {
		return libraryHistoryRepository.findByUser_UidOrderByOccurredAtDesc(SecurityUtils.uid(authentication));
	}
}
