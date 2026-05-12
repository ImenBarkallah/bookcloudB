package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.BookCatalogFiltersDto;
import com.bookcloud.smartlibrary.dto.BookCatalogItemDto;
import com.bookcloud.smartlibrary.dto.BookCatalogQuery;
import com.bookcloud.smartlibrary.dto.CreateBookRequest;
import com.bookcloud.smartlibrary.dto.PagedBooksResponse;
import com.bookcloud.smartlibrary.dto.UpdateBookRequest;
import com.bookcloud.smartlibrary.model.Book;

public interface BookService {
	List<Book> listAll();
	List<Book> listByCategory(Long categoryId);
	List<Book> listByAuthor(Long authorId);
	Book getById(Long id);
	Long create(CreateBookRequest req);
	void update(Long id, UpdateBookRequest req);
	List<BookCatalogItemDto> listFeatured(String userUid);
	void setFeatured(Long id, boolean featured);
	void delete(Long id);
	PagedBooksResponse searchCatalog(BookCatalogQuery query, String userUid);
	BookCatalogFiltersDto getCatalogFilters();
	List<BookCatalogItemDto> similarBooks(Long bookId, String userUid);
	List<BookCatalogItemDto> discoveryPopular(int limit, String userUid);
	List<BookCatalogItemDto> discoveryRecommendations(String userUid, int limit);
	List<BookCatalogItemDto> listFavorites(String userUid, int limit);
}
