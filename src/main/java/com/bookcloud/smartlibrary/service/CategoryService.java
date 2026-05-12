package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateCategoryRequest;
import com.bookcloud.smartlibrary.dto.PagedCategoriesResponse;
import com.bookcloud.smartlibrary.model.Category;

public interface CategoryService {

    List<Category> listAll();

    PagedCategoriesResponse listPaged(int page, int size, String search);

    Category getById(Long id);

    Category create(CreateCategoryRequest request);

    Category update(Long id, CreateCategoryRequest request);

    void delete(Long id);
}
