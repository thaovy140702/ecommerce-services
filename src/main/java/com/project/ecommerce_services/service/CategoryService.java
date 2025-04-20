package com.project.ecommerce_services.service;

import com.project.ecommerce_services.payload.category.CategoryDTO;
import com.project.ecommerce_services.payload.category.CategoryResponse;

public interface CategoryService {
	CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
