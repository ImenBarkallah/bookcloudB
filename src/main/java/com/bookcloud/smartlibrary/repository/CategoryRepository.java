package com.bookcloud.smartlibrary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.model.Category;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByNameAsc();

    @Query("""
        select c from Category c
        where (:search is null or
               lower(coalesce(c.name, '')) like lower(concat('%', :search, '%')) or
               lower(coalesce(c.description, '')) like lower(concat('%', :search, '%')))
        order by c.name asc
    """)
    Page<Category> search(@Param("search") String search, Pageable pageable);
}
