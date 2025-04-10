package com.project.ecommerce_services.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ecommerce_services.model.Category;

public interface CategoryRepository extends JpaRepository<Category,Long>{
	
    Category findByCategoryName(String categoryName);

}
