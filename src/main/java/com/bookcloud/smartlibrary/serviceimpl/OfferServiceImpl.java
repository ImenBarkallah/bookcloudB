package com.bookcloud.smartlibrary.serviceimpl;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.CreateOfferRequest;
import com.bookcloud.smartlibrary.dto.OfferAdminQuery;
import com.bookcloud.smartlibrary.dto.OfferDto;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.Category;
import com.bookcloud.smartlibrary.model.Loan;
import com.bookcloud.smartlibrary.model.Offer;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.CategoryRepository;
import com.bookcloud.smartlibrary.repository.LoanRepository;
import com.bookcloud.smartlibrary.repository.OfferRepository;
import com.bookcloud.smartlibrary.service.BookFavoriteService;
import com.bookcloud.smartlibrary.service.OfferService;

@Service
@Transactional
public class OfferServiceImpl implements OfferService {

	private final OfferRepository offerRepository;
	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final LoanRepository loanRepository;
	private final BookFavoriteService bookFavoriteService;

	public OfferServiceImpl(
			OfferRepository offerRepository,
			BookRepository bookRepository,
			CategoryRepository categoryRepository,
			LoanRepository loanRepository,
			BookFavoriteService bookFavoriteService) {
		this.offerRepository = offerRepository;
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
		this.loanRepository = loanRepository;
		this.bookFavoriteService = bookFavoriteService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OfferDto> listAllAdmin(OfferAdminQuery q) {
		String s = (q.getSearch() == null || q.getSearch().trim().isEmpty()) ? null : q.getSearch().trim();
		var page = offerRepository.searchAdmin(
				s,
				q.getType(),
				q.getActive(),
				PageRequest.of(Math.max(q.getPage(), 0), Math.max(q.getSize(), 1)));
		return page.getContent().stream()
				.filter(offer -> q.getExpired() == null || q.getExpired().booleanValue() == isExpired(offer))
				.map(offer -> toDto(offer, false))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OfferDto getByIdForUser(Long id) {
		return toDto(getOffer(id), false);
	}

	@Override
	@Transactional(readOnly = true)
	public OfferDto getByIdForAdmin(Long id) {
		return toDto(getOffer(id), false);
	}

	@Override
	public Long create(CreateOfferRequest req) {
		Offer offer = new Offer();
		applyOfferRequest(offer, req);
		return offerRepository.save(offer).getId();
	}

	@Override
	public Offer update(Long id, CreateOfferRequest req) {
		Offer offer = getOffer(id);
		applyOfferRequest(offer, req);
		return offerRepository.save(offer);
	}

	@Override
	public void delete(Long id) {
		offerRepository.delete(getOffer(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OfferDto> listActiveForUsers() {
		LocalDate today = LocalDate.now();
		return offerRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByCreatedAtDesc(today, today)
				.stream()
				.map(offer -> toDto(offer, false))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OfferDto> recommendedForUser(String userUid, int limit) {
		Set<Long> favoriteIds = new LinkedHashSet<>(bookFavoriteService.listAllFavoriteBookIds(userUid));
		Set<Long> categoryIds = favoriteIds.stream()
				.map(bookRepository::findById)
				.flatMap(java.util.Optional::stream)
				.map(Book::getCategoryId)
				.filter(java.util.Objects::nonNull)
				.collect(java.util.stream.Collectors.toSet());
		for (Loan loan : loanRepository.findByUser_UidOrderByBorrowedAtDesc(userUid)) {
			if (loan.getBookId() != null) {
				favoriteIds.add(loan.getBookId());
			}
			if (loan.getBook() != null && loan.getBook().getCategoryId() != null) {
				categoryIds.add(loan.getBook().getCategoryId());
			}
		}
		return offerRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
				.filter(offer -> offer.getRelatedBookIds().stream().anyMatch(favoriteIds::contains)
						|| offer.getCategoryIds().stream().anyMatch(categoryIds::contains))
				.limit(Math.max(1, limit))
				.map(offer -> toDto(offer, true))
				.toList();
	}

	private Offer getOffer(Long id) {
		return offerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
	}

	private void applyOfferRequest(Offer offer, CreateOfferRequest req) {
		offer.setTitle(req.getTitle());
		offer.setDescription(req.getDescription());
		offer.setImageUrl(req.getImageUrl());
		offer.setType(req.getType());
		offer.setStartDate(req.getStartDate());
		offer.setEndDate(req.getEndDate());
		offer.setActive(req.isActive());
		offer.setRelatedBooks(resolveBooks(req.getRelatedBookIds()));
		offer.setCategories(resolveCategories(req.getCategoryIds()));
	}

	private Set<Book> resolveBooks(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return new LinkedHashSet<>();
		}
		Set<Book> books = new LinkedHashSet<>(bookRepository.findAllById(ids));
		if (books.size() != ids.size()) {
			throw new ResourceNotFoundException("Related book not found");
		}
		return books;
	}

	private Set<Category> resolveCategories(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return new LinkedHashSet<>();
		}
		Set<Category> categories = new LinkedHashSet<>(categoryRepository.findAllById(ids));
		if (categories.size() != ids.size()) {
			throw new ResourceNotFoundException("Related category not found");
		}
		return categories;
	}

	private OfferDto toDto(Offer offer, boolean personalized) {
		OfferDto dto = new OfferDto();
		dto.setId(offer.getId());
		dto.setTitle(offer.getTitle());
		dto.setDescription(offer.getDescription());
		dto.setImageUrl(offer.getImageUrl());
		dto.setType(offer.getType());
		dto.setStartDate(offer.getStartDate());
		dto.setEndDate(offer.getEndDate());
		dto.setActive(offer.isActive());
		dto.setExpired(isExpired(offer));
		dto.setPersonalized(personalized);
		dto.setRelatedBookIds(offer.getRelatedBookIds());
		dto.setCategoryIds(offer.getCategoryIds());
		return dto;
	}

	private boolean isExpired(Offer offer) {
		return offer.getEndDate() != null && offer.getEndDate().isBefore(LocalDate.now());
	}
}
