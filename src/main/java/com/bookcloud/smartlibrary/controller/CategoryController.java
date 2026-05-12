package com.bookcloud.smartlibrary.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateCategoryRequest;
import com.bookcloud.smartlibrary.dto.PagedCategoriesResponse;
import com.bookcloud.smartlibrary.model.Category;
import com.bookcloud.smartlibrary.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	@PreAuthorize("permitAll()")
	public List<Category> list() {
		return categoryService.listAll();
	}

	@GetMapping("/paged")
	@PreAuthorize("permitAll()")
	public PagedCategoriesResponse listPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size,
			@RequestParam(required = false) String search) {
		return categoryService.listPaged(page, size, search);
	}

	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public Category get(@PathVariable Long id) {
		return categoryService.getById(id);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Category> create(@Valid @ModelAttribute CreateCategoryRequest req) {
		Category created = categoryService.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Category> update(@PathVariable Long id, @Valid @ModelAttribute CreateCategoryRequest req) {
		return ResponseEntity.ok(categoryService.update(id, req));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		categoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
