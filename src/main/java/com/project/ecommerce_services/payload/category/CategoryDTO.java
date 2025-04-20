package com.project.ecommerce_services.payload.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CategoryDTO {
	private Long categoryId;
	
	@NotBlank(message = "Category name cannot be blank")
	@Size(min = 5, message = "Category name must be at least 5 characters long")
    private String categoryName;
	
}
