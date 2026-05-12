package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateLibraryBranchRequest;
import com.bookcloud.smartlibrary.dto.UpdateLibraryBranchRequest;
import com.bookcloud.smartlibrary.model.LibraryBranch;
import com.bookcloud.smartlibrary.service.LibraryBranchService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/library-branches")
public class LibraryBranchController {

	private final LibraryBranchService libraryBranchService;

	public LibraryBranchController(LibraryBranchService libraryBranchService) {
		this.libraryBranchService = libraryBranchService;
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<LibraryBranch> list() {
		return libraryBranchService.listAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public LibraryBranch get(@PathVariable Long id) {
		return libraryBranchService.getById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public LibraryBranch create(@Valid @RequestBody CreateLibraryBranchRequest req, Authentication authentication) {
		return libraryBranchService.create(req, SecurityUtils.currentRole(authentication));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public LibraryBranch update(@PathVariable Long id, @Valid @RequestBody UpdateLibraryBranchRequest req,
			Authentication authentication) {
		return libraryBranchService.update(id, req, SecurityUtils.currentRole(authentication));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(@PathVariable Long id, Authentication authentication) {
		libraryBranchService.delete(id, SecurityUtils.currentRole(authentication));
	}
}
