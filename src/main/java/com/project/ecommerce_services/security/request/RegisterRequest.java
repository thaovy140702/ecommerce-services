package com.project.ecommerce_services.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
    private String email;
	
	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
	
	@NotBlank(message = "Firstname is required")
	@Size(min = 2, message = "Firstname must be at least 2 characters long")
    private String firstName;
	
	@NotBlank(message = "Lastname is required")
	@Size(min = 2, message = "Lastname must be at least 2 characters long")
    private String lastName;
	
	@NotBlank(message = "Address is required")
	@Size(max = 255, message = "Address must be at most 255 characters long")
    private String address;
	
	@Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

}
