package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

	List<Author> findAllByOrderByNameAsc();
}
