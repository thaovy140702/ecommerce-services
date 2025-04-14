package com.project.ecommerce_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class EcommerceServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceServicesApplication.class, args);
	}

}
