package com.project.ecommerce_services.repository;

import com.project.ecommerce_services.model.Role;
import com.project.ecommerce_services.model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}