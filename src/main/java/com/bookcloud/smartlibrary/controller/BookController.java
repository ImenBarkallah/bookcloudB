package com.bookcloud.smartlibrary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.BookCatalogItemDto;
import com.bookcloud.smartlibrary.dto.BookCatalogFiltersDto;
import com.bookcloud.smartlibrary.dto.BookCatalogQuery;
import com.bookcloud.smartlibrary.dto.CreateBookCopyRequest;
import com.bookcloud.smartlibrary.dto.CreateBookRequest;
import com.bookcloud.smartlibrary.dto.FavoriteToggleResponse;
import com.bookcloud.smartlibrary.dto.PagedBooksResponse;
import com.bookcloud.smartlibrary.dto.UpdateBookRequest;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.service.BookCopyService;
import com.bookcloud.smartlibrary.service.BookFavoriteService;
import com.bookcloud.smartlibrary.service.BookService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;
	private final BookCopyService bookCopyService;
	private final BookFavoriteService bookFavoriteService;

	public BookController(BookService bookService, BookCopyService bookCopyService,
			BookFavoriteService bookFavoriteService) {
		this.bookService = bookService;
		this.bookCopyService = bookCopyService;
		this.bookFavoriteService = bookFavoriteService;
	}

	@GetMapping("/catalog/paged")
	@PreAuthorize("permitAll()")
	public PagedBooksResponse paged(BookCatalogQuery query, Authentication authentication) {
		String uid = authentication != null && authentication.isAuthenticated() ? SecurityUtils.uid(authentication)
				: null;
		return bookService.searchCatalog(query, uid);
	}

	@GetMapping("/catalog/filters")
	@PreAuthorize("permitAll()")
	public BookCatalogFiltersDto catalogFilters() {
		return bookService.getCatalogFilters();
	}

	@GetMapping("/{id}/similar")
	@PreAuthorize("permitAll()")
	public List<BookCatalogItemDto> similar(@PathVariable Long id, Authentication authentication) {
		String uid = authentication != null && authentication.isAuthenticated() ? SecurityUtils.uid(authentication)
				: null;
		return bookService.similarBooks(id, uid);
	}

	@GetMapping("/featured")
	@PreAuthorize("permitAll()")
	public List<BookCatalogItemDto> featured(Authentication authentication) {
		String uid = authentication != null && authentication.isAuthenticated() ? SecurityUtils.uid(authentication)
				: null;
		return bookService.listFeatured(uid);
	}

	@PostMapping("/{bookId}/favorite")
	@PreAuthorize("isAuthenticated()")
	public FavoriteToggleResponse toggleFavorite(@PathVariable Long bookId, Authentication authentication) {
		boolean favorited = bookFavoriteService.toggle(SecurityUtils.uid(authentication), bookId);
		return new FavoriteToggleResponse(bookId, favorited);
	}

	@GetMapping
	@PreAuthorize("permitAll()")
	public List<Book> list(@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long authorId) {
		if (categoryId != null) {
			return bookService.listByCategory(categoryId);
		}
		if (authorId != null) {
			return bookService.listByAuthor(authorId);
		}
		return bookService.listAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public Book get(@PathVariable Long id) {
		return bookService.getById(id);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Map<String, Long>> create(@Valid @ModelAttribute CreateBookRequest req) {
		Long id = bookService.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> update(@PathVariable Long id, @Valid @ModelAttribute UpdateBookRequest req) {
		bookService.update(id, req);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/featured/{featured}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> setFeatured(@PathVariable Long id, @PathVariable boolean featured) {
		bookService.setFeatured(id, featured);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		bookService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{bookId}/copies")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Map<String, Long>> registerCopy(@PathVariable Long bookId,
			@Valid @RequestBody CreateBookCopyRequest req) {
		Long copyId = bookCopyService.registerCopy(bookId, req.getBranchId(), req.getBarcode());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", copyId));
	}
}
