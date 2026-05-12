package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateLibraryBranchRequest;
import com.bookcloud.smartlibrary.dto.UpdateLibraryBranchRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.LibraryBranch;

public interface LibraryBranchService {
	List<LibraryBranch> listAll();
	LibraryBranch getById(Long id);
	LibraryBranch create(CreateLibraryBranchRequest req, Role role);
	LibraryBranch update(Long id, UpdateLibraryBranchRequest req, Role role);
	void delete(Long id, Role role);
}
