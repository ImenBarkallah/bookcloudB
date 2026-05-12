package com.bookcloud.smartlibrary.service;

public interface BookCopyService {
	Long registerCopy(Long bookId, Long branchId, String barcode);
}
