package com.bookcloud.smartlibrary.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.config.CloudinaryProperties;
import com.bookcloud.smartlibrary.dto.BookCatalogFiltersDto;
import com.bookcloud.smartlibrary.dto.BookCatalogItemDto;
import com.bookcloud.smartlibrary.dto.BookCatalogQuery;
import com.bookcloud.smartlibrary.dto.CatalogFilterOptionDto;
import com.bookcloud.smartlibrary.dto.CreateBookRequest;
import com.bookcloud.smartlibrary.dto.PagedBooksResponse;
import com.bookcloud.smartlibrary.dto.UpdateBookRequest;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Author;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.Category;
import com.bookcloud.smartlibrary.model.LibraryBranch;
import com.bookcloud.smartlibrary.repository.AuthorRepository;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.CategoryRepository;
import com.bookcloud.smartlibrary.repository.LibraryBranchRepository;
import com.bookcloud.smartlibrary.service.BookFavoriteService;
import com.bookcloud.smartlibrary.service.BookService;

@Service
@Transactional
public class BookServiceImpl implements BookService {

	private static final int MAX_FEATURED = 5;

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final AuthorRepository authorRepository;
	private final LibraryBranchRepository libraryBranchRepository;
	private final BookFavoriteService bookFavoriteService;
	private final CloudinaryImageService cloudinaryImageService;
	private final CloudinaryProperties cloudinaryProperties;

	public BookServiceImpl(
			BookRepository bookRepository,
			CategoryRepository categoryRepository,
			AuthorRepository authorRepository,
			LibraryBranchRepository libraryBranchRepository,
			BookFavoriteService bookFavoriteService,
			CloudinaryImageService cloudinaryImageService,
			CloudinaryProperties cloudinaryProperties) {
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
		this.authorRepository = authorRepository;
		this.libraryBranchRepository = libraryBranchRepository;
		this.bookFavoriteService = bookFavoriteService;
		this.cloudinaryImageService = cloudinaryImageService;
		this.cloudinaryProperties = cloudinaryProperties;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Book> listAll() {
		return bookRepository.findAllByOrderByCreatedAtDesc();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Book> listByCategory(Long categoryId) {
		return bookRepository.findByCategory_Id(categoryId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Book> listByAuthor(Long authorId) {
		return bookRepository.findByAuthors_Id(authorId);
	}

	@Override
	@Transactional(readOnly = true)
	public Book getById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found"));
	}

	@Override
	public Long create(CreateBookRequest req) {
		Category category = categoryRepository.findById(req.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		Set<Author> authors = resolveAuthors(req.getAuthorIds());
		LibraryBranch branch = resolveBranch(req.getDefaultBranchId());
		Book book = new Book();
		book.setTitle(req.getTitle());
		book.setDescription(req.getDescription());
		book.setAuthors(authors);
		book.setAuthor(joinAuthorNames(authors));
		book.setIsbn(req.getIsbn());
		book.setCategory(category);
		book.setTotalCopies(req.getTotalCopies());
		book.setAvailableCopies(req.getTotalCopies());
		book.setDefaultBranch(branch);
		book.setPublicationYear(req.getPublicationYear());
		book.setLanguage(req.getLanguage());
		book.setPublisher(req.getPublisher());
		book.setFeatured(req.isFeatured());
		if (book.isFeatured()) {
			ensureFeaturedLimit(null);
		}
		book = bookRepository.save(book);
		applyCover(book, req.getCover());
		return book.getId();
	}

	@Override
	public void update(Long id, UpdateBookRequest req) {
		Book book = getById(id);
		if (req.getTitle() != null) {
			book.setTitle(req.getTitle());
		}
		if (req.getDescription() != null) {
			book.setDescription(req.getDescription());
		}
		if (req.getAuthorIds() != null) {
			Set<Author> authors = resolveAuthors(req.getAuthorIds());
			book.setAuthors(authors);
			book.setAuthor(joinAuthorNames(authors));
		}
		if (req.getIsbn() != null) {
			book.setIsbn(req.getIsbn());
		}
		if (req.getCategoryId() != null) {
			book.setCategory(categoryRepository.findById(req.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Category not found")));
		}
		if (req.getTotalCopies() != null) {
			int delta = req.getTotalCopies() - book.getTotalCopies();
			book.setTotalCopies(req.getTotalCopies());
			book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + delta));
		}
		if (req.getDefaultBranchId() != null) {
			book.setDefaultBranch(resolveBranch(req.getDefaultBranchId()));
		}
		if (req.getPublicationYear() != null) {
			book.setPublicationYear(req.getPublicationYear());
		}
		if (req.getLanguage() != null) {
			book.setLanguage(req.getLanguage());
		}
		if (req.getPublisher() != null) {
			book.setPublisher(req.getPublisher());
		}
		if (req.getFeatured() != null) {
			if (req.getFeatured() && !book.isFeatured()) {
				ensureFeaturedLimit(id);
			}
			book.setFeatured(req.getFeatured());
		}
		applyCover(book, req.getCover());
		bookRepository.save(book);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookCatalogItemDto> listFeatured(String userUid) {
		Set<Long> favorites = new LinkedHashSet<>(bookFavoriteService.listAllFavoriteBookIds(userUid));
		return bookRepository.findByFeaturedTrueAndHiddenFalse().stream()
				.sorted(Comparator.comparing(Book::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
				.limit(MAX_FEATURED)
				.map(book -> toCatalogDto(book, favorites.contains(book.getId())))
				.toList();
	}

	@Override
	public void setFeatured(Long id, boolean featured) {
		Book book = getById(id);
		if (featured && !book.isFeatured()) {
			ensureFeaturedLimit(id);
		}
		book.setFeatured(featured);
		bookRepository.save(book);
	}

	@Override
	public void delete(Long id) {
		bookRepository.delete(getById(id));
	}

	private void applyCover(Book book, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return;
		}
		String url = cloudinaryImageService.upload(
				file,
				cloudinaryProperties.getBookFolder(),
				"book",
				book.getId());
		book.setCoverUrl(url);
		bookRepository.save(book);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedBooksResponse searchCatalog(BookCatalogQuery query, String userUid) {
		int size = Math.min(Math.max(query.getSize(), 1), 48);
		int page = Math.max(query.getPage(), 0);
		Set<Long> favorites = new LinkedHashSet<>(bookFavoriteService.listAllFavoriteBookIds(userUid));

		List<Book> filtered = bookRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(book -> !book.isHidden())
				.filter(book -> query.getCategoryId() == null || query.getCategoryId().equals(book.getCategoryId()))
				.filter(book -> matchesSearch(book, query.getSearch()))
				.filter(book -> query.getLanguage() == null || query.getLanguage().isBlank()
						|| equalsIgnoreCase(query.getLanguage(), book.getLanguage()))
				.filter(book -> query.getYearFrom() == null || book.getPublicationYear() != null
						&& book.getPublicationYear() >= query.getYearFrom())
				.filter(book -> query.getYearTo() == null || book.getPublicationYear() != null
						&& book.getPublicationYear() <= query.getYearTo())
				.filter(book -> !Boolean.TRUE.equals(query.getAvailableOnly()) || book.getAvailableCopies() > 0)
				.filter(book -> query.getMinRating() == null || pseudoRating(book.getId()) >= query.getMinRating())
				.filter(book -> query.getFormat() == null || query.getFormat().isBlank()
						|| query.getFormat().equalsIgnoreCase(resolveFormat(book)))
				.sorted(resolveSort(query.getSort()))
				.toList();

		long total = filtered.size();
		int from = Math.min(page * size, filtered.size());
		int to = Math.min(from + size, filtered.size());
		List<BookCatalogItemDto> content = filtered.subList(from, to).stream()
				.map(book -> toCatalogDto(book, favorites.contains(book.getId())))
				.toList();
		int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
		return new PagedBooksResponse(content, total, totalPages, page, size);
	}

	@Override
	@Transactional(readOnly = true)
	public BookCatalogFiltersDto getCatalogFilters() {
		List<Book> visibleBooks = bookRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(book -> !book.isHidden())
				.toList();

		Set<String> languages = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		Integer minYear = null;
		Integer maxYear = null;
		for (Book book : visibleBooks) {
			if (book.getLanguage() != null && !book.getLanguage().isBlank()) {
				languages.add(book.getLanguage().trim());
			}
			Integer year = book.getPublicationYear();
			if (year != null) {
				minYear = minYear == null ? year : Math.min(minYear, year);
				maxYear = maxYear == null ? year : Math.max(maxYear, year);
			}
		}

		List<CatalogFilterOptionDto> categories = categoryRepository.findAllByOrderByNameAsc().stream()
				.map(category -> new CatalogFilterOptionDto(category.getId(), category.getName()))
				.toList();

		BookCatalogFiltersDto dto = new BookCatalogFiltersDto();
		dto.setCategories(categories);
		dto.setFormats(List.of("PHYSICAL", "EBOOK", "AUDIO"));
		dto.setLanguages(new ArrayList<>(languages));
		dto.setMinPublicationYear(minYear);
		dto.setMaxPublicationYear(maxYear);
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookCatalogItemDto> similarBooks(Long bookId, String userUid) {
		Book book = getById(bookId);
		Set<Long> favorites = new LinkedHashSet<>(bookFavoriteService.listAllFavoriteBookIds(userUid));
		return bookRepository.findSimilarBooks(book.getCategoryId(), bookId, org.springframework.data.domain.PageRequest.of(0, 4))
				.stream()
				.map(item -> toCatalogDto(item, favorites.contains(item.getId())))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookCatalogItemDto> discoveryPopular(int limit, String userUid) {
		Set<Long> favorites = new LinkedHashSet<>(bookFavoriteService.listAllFavoriteBookIds(userUid));
		return bookRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(book -> !book.isHidden())
				.filter(book -> isPopular(book.getId()))
				.limit(Math.max(1, Math.min(limit, 48)))
				.map(book -> toCatalogDto(book, favorites.contains(book.getId())))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookCatalogItemDto> discoveryRecommendations(String userUid, int limit) {
		List<Long> favoriteIds = bookFavoriteService.listAllFavoriteBookIds(userUid);
		if (favoriteIds.isEmpty()) {
			return List.of();
		}
		Set<Long> favoriteSet = new LinkedHashSet<>(favoriteIds);
		Set<Long> categoryIds = favoriteIds.stream()
				.map(bookRepository::findById)
				.flatMap(java.util.Optional::stream)
				.map(Book::getCategoryId)
				.filter(java.util.Objects::nonNull)
				.collect(Collectors.toSet());
		Set<Long> authorIds = favoriteIds.stream()
				.map(bookRepository::findById)
				.flatMap(java.util.Optional::stream)
				.map(Book::getAuthorId)
				.filter(java.util.Objects::nonNull)
				.collect(Collectors.toSet());
		return bookRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(book -> !book.isHidden())
				.filter(book -> !favoriteSet.contains(book.getId()))
				.filter(book -> categoryIds.contains(book.getCategoryId()) || authorIds.contains(book.getAuthorId()))
				.limit(Math.max(1, Math.min(limit, 48)))
				.map(book -> toCatalogDto(book, false))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookCatalogItemDto> listFavorites(String userUid, int limit) {
		List<Long> favoriteIds = bookFavoriteService.listAllFavoriteBookIds(userUid);
		return favoriteIds.stream()
				.map(bookRepository::findById)
				.flatMap(java.util.Optional::stream)
				.limit(Math.max(1, Math.min(limit, 200)))
				.map(book -> toCatalogDto(book, true))
				.toList();
	}

	private Set<Author> resolveAuthors(List<Long> authorIds) {
		if (authorIds == null || authorIds.isEmpty()) {
			throw new BusinessRuleException("At least one author is required");
		}
		Set<Author> authors = new LinkedHashSet<>(authorRepository.findAllById(authorIds));
		if (authors.size() != authorIds.size()) {
			throw new ResourceNotFoundException("Author not found");
		}
		return authors;
	}

	private LibraryBranch resolveBranch(Long branchId) {
		if (branchId == null) {
			return null;
		}
		return libraryBranchRepository.findById(branchId)
				.orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
	}

	private void ensureFeaturedLimit(Long currentBookId) {
		long count = bookRepository.findByFeaturedTrueAndHiddenFalse().stream()
				.filter(book -> currentBookId == null || !currentBookId.equals(book.getId()))
				.count();
		if (count >= MAX_FEATURED) {
			throw new BusinessRuleException("Featured book limit reached");
		}
	}

	private String joinAuthorNames(Set<Author> authors) {
		return authors.stream()
				.map(Author::getName)
				.filter(java.util.Objects::nonNull)
				.map(String::trim)
				.filter(value -> !value.isEmpty())
				.collect(Collectors.joining(", "));
	}

	private boolean matchesSearch(Book book, String search) {
		if (search == null || search.isBlank()) {
			return true;
		}
		String query = search.toLowerCase(Locale.ROOT);
		return contains(book.getTitle(), query)
				|| contains(book.getDescription(), query)
				|| contains(book.getAuthor(), query);
	}

	private boolean contains(String source, String query) {
		return source != null && source.toLowerCase(Locale.ROOT).contains(query);
	}

	private boolean equalsIgnoreCase(String a, String b) {
		return a != null && b != null && a.equalsIgnoreCase(b);
	}

	private Comparator<Book> resolveSort(String sort) {
		String value = sort == null ? "newest" : sort;
		return switch (value) {
		case "title" -> Comparator.comparing(book -> nullableString(book.getTitle()), String.CASE_INSENSITIVE_ORDER);
		case "year" -> Comparator.comparing(Book::getPublicationYear, Comparator.nullsLast(Comparator.reverseOrder()));
		case "rating" -> Comparator.comparing((Book book) -> pseudoRating(book.getId())).reversed();
		default -> Comparator.comparing(Book::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
		};
	}

	private String nullableString(String value) {
		return value == null ? "" : value;
	}

	private BookCatalogItemDto toCatalogDto(Book book, boolean favorited) {
		BookCatalogItemDto dto = new BookCatalogItemDto();
		dto.setId(book.getId());
		dto.setTitle(book.getTitle());
		dto.setAuthor(book.getAuthor());
		dto.setCategoryId(book.getCategoryId());
		dto.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : "");
		dto.setCoverUrl(buildCoverUrl(book));
		dto.setPublicationYear(book.getPublicationYear());
		dto.setLanguage(book.getLanguage());
		dto.setPublisher(book.getPublisher());
		dto.setIsbn(book.getIsbn());
		dto.setTotalCopies(book.getTotalCopies());
		dto.setAvailableCopies(book.getAvailableCopies());
		dto.setNew(book.getCreatedAt() != null && book.getCreatedAt().isAfter(java.time.Instant.now().minusSeconds(30L * 24 * 3600)));
		dto.setPopular(isPopular(book.getId()));
		dto.setAverageRating(pseudoRating(book.getId()));
		dto.setRatingCount(12 + Math.abs(String.valueOf(book.getId()).hashCode()) % 400);
		dto.setIsFavorited(favorited);
		dto.setMediaFormat(resolveFormat(book));
		dto.setFeatured(book.isFeatured());
		return dto;
	}

	private String resolveFormat(Book book) {
		return book.getMediaFormat() != null && !book.getMediaFormat().isBlank()
				? book.getMediaFormat().toUpperCase(Locale.ROOT)
				: "PHYSICAL";
	}

	private boolean isPopular(Long id) {
		return Math.abs(String.valueOf(id).hashCode()) % 11 == 0;
	}

	private double pseudoRating(Long id) {
		int hash = String.valueOf(id).hashCode();
		double value = 3.5 + (Math.abs(hash) % 15) / 10.0;
		return Math.round(value * 10) / 10.0;
	}

	private String buildCoverUrl(Book book) {
		if (book.getCoverUrl() != null && !book.getCoverUrl().isBlank()) {
			return book.getCoverUrl();
		}
		if (book.getIsbn() == null || book.getIsbn().isBlank()) {
			return null;
		}
		String digits = book.getIsbn().replace("-", "").replace(" ", "");
		return digits.isBlank() ? null : "https://covers.openlibrary.org/b/isbn/" + digits + "-M.jpg";
	}
}
