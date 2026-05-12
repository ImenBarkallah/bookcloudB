package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateAuthorRequest;
import com.bookcloud.smartlibrary.dto.UpdateAuthorRequest;
import com.bookcloud.smartlibrary.model.Author;

public interface AuthorService {
	List<Author> listAll();
	Author getById(Long id);
	Long create(CreateAuthorRequest req);
	void update(Long id, UpdateAuthorRequest req);
	void delete(Long id);
}
