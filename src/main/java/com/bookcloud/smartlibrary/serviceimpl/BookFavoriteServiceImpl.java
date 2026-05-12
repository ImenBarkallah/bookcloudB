package com.bookcloud.smartlibrary.serviceimpl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.FavoriteBook;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.FavoriteBookRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.BookFavoriteService;

@Service
@Transactional
public class BookFavoriteServiceImpl implements BookFavoriteService {

	private final UserRepository userRepository;
	private final BookRepository bookRepository;
	private final FavoriteBookRepository favoriteBookRepository;

	public BookFavoriteServiceImpl(
			UserRepository userRepository,
			BookRepository bookRepository,
			FavoriteBookRepository favoriteBookRepository) {
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
		this.favoriteBookRepository = favoriteBookRepository;
	}

	@Override
	public boolean toggle(String userUid, Long bookId) {
		AppUser user = userRepository.findByUid(userUid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found"));

		var existing = favoriteBookRepository.findByUser_UidAndBook_Id(userUid, bookId);
		if (existing.isPresent()) {
			favoriteBookRepository.delete(existing.get());
			return false;
		}

		FavoriteBook favoriteBook = new FavoriteBook();
		favoriteBook.setUser(user);
		favoriteBook.setBook(book);
		favoriteBookRepository.save(favoriteBook);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Set<Long> favoriteIdsAmong(String userUid, List<Long> bookIds) {
		if (userUid == null || userUid.isBlank() || bookIds == null || bookIds.isEmpty()) {
			return Set.of();
		}
		return new LinkedHashSet<>(favoriteBookRepository.findFavoriteBookIdsByUserUid(userUid).stream()
				.filter(bookIds::contains)
				.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> listAllFavoriteBookIds(String userUid) {
		if (userUid == null || userUid.isBlank()) {
			return List.of();
		}
		return favoriteBookRepository.findFavoriteBookIdsByUserUid(userUid);
	}

	@Override
	@Transactional(readOnly = true)
	public int countFavorites(String userUid) {
		if (userUid == null || userUid.isBlank()) {
			return 0;
		}
		return (int) favoriteBookRepository.countByUserUid(userUid);
	}
}
