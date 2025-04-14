package com.project.ecommerce_services.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
	private final Cloudinary cloudinary;

    public CloudinaryService(
    		 @Value("${cloudinary.cloud_name}") String cloudName,
    	     @Value("${cloudinary.api_key}") String apiKey,
    	     @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }

    
    @Async
    public CompletableFuture<Map<String, Object>> uploadFileAsync(MultipartFile file) throws IOException {
        @SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return CompletableFuture.completedFuture(result);
    }

    public Map<String, Object> deleteFile(String publicId) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return result;
    }


}
