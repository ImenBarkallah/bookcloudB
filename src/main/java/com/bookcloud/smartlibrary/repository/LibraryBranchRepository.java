package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.model.LibraryBranch;

public interface LibraryBranchRepository extends JpaRepository<LibraryBranch, Long> {

	List<LibraryBranch> findAllByOrderByNameAsc();
}
