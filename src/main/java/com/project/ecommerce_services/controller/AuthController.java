package com.project.ecommerce_services.controller;

import com.project.ecommerce_services.exceptions.APIException;
import com.project.ecommerce_services.exceptions.ResourceNotFoundException;
import com.project.ecommerce_services.model.Customer;
import com.project.ecommerce_services.model.Role;
import com.project.ecommerce_services.model.User;
import com.project.ecommerce_services.model.enums.ERole;
import com.project.ecommerce_services.payload.APIResponse;
import com.project.ecommerce_services.payload.ProfileUpdateRequest;
import com.project.ecommerce_services.repository.CustomerRepository;
import com.project.ecommerce_services.repository.RoleRepository;
import com.project.ecommerce_services.repository.UserRepository;
import com.project.ecommerce_services.security.JwtTokenProvider;
import com.project.ecommerce_services.security.request.LoginRequest;
import com.project.ecommerce_services.security.request.RegisterRequest;
import com.project.ecommerce_services.security.response.LoginResponse;
import com.project.ecommerce_services.security.response.UserInfoResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                         JwtTokenProvider jwtTokenProvider,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         CustomerRepository customerRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String jwt = jwtTokenProvider.generateToken(authentication);
        Cookie jwtCookie = new Cookie("jwtCookie", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); 
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);

        return new ResponseEntity<>(new LoginResponse(jwt), HttpStatus.OK);
    }


    @Transactional
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
        	throw new APIException("Email is already taken!");
        }

        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Role",ERole.ROLE_CUSTOMER.toString()));

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)
                .build();

        User savedUser = userRepository.save(user);

        Customer customer = Customer.builder()
                .userId(savedUser.getId())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .address(registerRequest.getAddress())
                .phone(registerRequest.getPhone())
                .build();

        customerRepository.save(customer);

        return ResponseEntity.ok("User registered successfully!");
    }
    
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwtCookie", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); 
        response.addCookie(jwtCookie);
        return ResponseEntity.ok("Logged out successfully!");
    }
    
    
    @GetMapping("/user/profile")
    public ResponseEntity<UserInfoResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        Customer customer = customerRepository.findById(user.getId())
                .orElse(new Customer());

        UserInfoResponse response = new UserInfoResponse(
                user.getEmail(),
                user.getRole().getName().name(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.getPhone()
        );

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PutMapping("/user/profile")
    public ResponseEntity<APIResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", user.getId()));
        
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);

        return ResponseEntity.ok(new APIResponse("Profile updated successfully!", true));
    }
}