package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.enums.NewsType;
import com.bookcloud.smartlibrary.model.News;

public interface NewsRepository extends JpaRepository<News, Long> {

	List<News> findByActiveTrueOrderByCreatedAtDesc();

	List<News> findByActiveTrueAndTypeOrderByCreatedAtDesc(NewsType type);

	List<News> findByActiveOrderByCreatedAtDesc(boolean active);

	List<News> findByActiveAndTypeOrderByCreatedAtDesc(boolean active, NewsType type);

	List<News> findAllByOrderByCreatedAtDesc();
}
