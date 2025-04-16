package com.project.ecommerce_services.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
	
	@Id
    private Long userId;

    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    
//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user; 
}

