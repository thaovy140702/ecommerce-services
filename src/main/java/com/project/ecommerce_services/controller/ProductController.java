package com.project.ecommerce_services.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.ecommerce_services.config.AppConstants;
import com.project.ecommerce_services.payload.ProductDTO;
import com.project.ecommerce_services.payload.ProductResponse;
import com.project.ecommerce_services.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProductController {
	
	private ProductService productService;

	public ProductController(ProductService productService) {
        this.productService = productService;
    }
	
	@PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    
    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
    
    @GetMapping("/public/products/featured")
    public ResponseEntity<ProductResponse> getFeaturedProduct(
    		 @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
             @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
             @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
             @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder)
    {
    	ProductResponse productResponse = productService.getFeaturedProducts(pageNumber, pageSize, sortBy, sortOrder);
    	return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }


    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                 @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        ProductResponse productResponse = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        ProductResponse productResponse = productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId){
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
