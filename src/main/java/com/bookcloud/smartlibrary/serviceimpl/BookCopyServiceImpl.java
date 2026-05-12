package com.bookcloud.smartlibrary.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.enums.BookCopyStatus;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.BookCopy;
import com.bookcloud.smartlibrary.model.LibraryBranch;
import com.bookcloud.smartlibrary.repository.BookCopyRepository;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.LibraryBranchRepository;
import com.bookcloud.smartlibrary.service.BookCopyService;

@Service
@Transactional
public class BookCopyServiceImpl implements BookCopyService {

	private final BookCopyRepository bookCopyRepository;
	private final BookRepository bookRepository;
	private final LibraryBranchRepository libraryBranchRepository;

	public BookCopyServiceImpl(
			BookCopyRepository bookCopyRepository,
			BookRepository bookRepository,
			LibraryBranchRepository libraryBranchRepository) {
		this.bookCopyRepository = bookCopyRepository;
		this.bookRepository = bookRepository;
		this.libraryBranchRepository = libraryBranchRepository;
	}

	@Override
	public Long registerCopy(Long bookId, Long branchId, String barcode) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found"));
		LibraryBranch branch = libraryBranchRepository.findById(branchId)
				.orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
		BookCopy copy = new BookCopy();
		copy.setBook(book);
		copy.setBranch(branch);
		copy.setBarcode(barcode);
		copy.setStatus(BookCopyStatus.AVAILABLE);
		BookCopy saved = bookCopyRepository.save(copy);
		book.setTotalCopies(book.getTotalCopies() + 1);
		book.setAvailableCopies(book.getAvailableCopies() + 1);
		bookRepository.save(book);
		return saved.getId();
	}
}
