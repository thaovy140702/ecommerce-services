package com.project.ecommerce_services.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.project.ecommerce_services.payload.product.ProductDTO;
import com.project.ecommerce_services.payload.product.ProductResponse;

import jakarta.validation.Valid;

public interface ProductService {

	ProductDTO addProduct(Long categoryId, ProductDTO product);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    
    ProductDTO getProductById(Long productId);

    ProductResponse getFeaturedProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO product);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

}
