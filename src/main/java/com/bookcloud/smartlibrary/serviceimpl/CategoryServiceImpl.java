package com.bookcloud.smartlibrary.serviceimpl;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookcloud.smartlibrary.config.CloudinaryProperties;
import com.bookcloud.smartlibrary.dto.CreateCategoryRequest;
import com.bookcloud.smartlibrary.dto.PagedCategoriesResponse;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Category;
import com.bookcloud.smartlibrary.repository.CategoryRepository;
import com.bookcloud.smartlibrary.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CloudinaryImageService cloudinaryImageService;
    private final CloudinaryProperties cloudinaryProperties;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CloudinaryImageService cloudinaryImageService,
            CloudinaryProperties cloudinaryProperties
    ) {
        this.categoryRepository = categoryRepository;
        this.cloudinaryImageService = cloudinaryImageService;
        this.cloudinaryProperties = cloudinaryProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> listAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedCategoriesResponse listPaged(int page, int size, String search) {
        String keyword = normalize(search);

        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1)
        );

        Page<Category> result = categoryRepository.search(keyword, pageRequest);

        return new PagedCategoriesResponse(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category create(CreateCategoryRequest request) {
        Category category = new Category();

        category.setName(normalize(request.getName()));
        category.setDescription(normalize(request.getDescription()));

        category = categoryRepository.save(category);

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = cloudinaryImageService.upload(
                    request.getImage(),
                    cloudinaryProperties.getCategoryFolder(),
                    "category",
                    category.getId()
            );

            category.setImageUrl(imageUrl);
            category = categoryRepository.save(category);
        }

        return category;
    }

    @Override
    public Category update(Long id, CreateCategoryRequest request) {
        Category category = getById(id);

        category.setName(normalize(request.getName()));
        category.setDescription(normalize(request.getDescription()));

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = cloudinaryImageService.upload(
                    request.getImage(),
                    cloudinaryProperties.getCategoryFolder(),
                    "category",
                    category.getId()
            );

            category.setImageUrl(imageUrl);
        }

        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }

    private String normalize(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
