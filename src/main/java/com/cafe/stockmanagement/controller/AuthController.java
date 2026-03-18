package com.cafe.stockmanagement.controller;

import com.cafe.stockmanagement.dto.request.LoginRequest;
import com.cafe.stockmanagement.dto.request.RegisterRequest;
import com.cafe.stockmanagement.dto.response.ApiResponse;
import com.cafe.stockmanagement.dto.response.AuthResponse;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.UserRepository;
import com.cafe.stockmanagement.security.JwtTokenProvider;
import com.cafe.stockmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cafe.stockmanagement.entity.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(
            ApiResponse.success("User registered successfully", response)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success("Login successful", response)
        );
    }

@GetMapping("/oauth2/success")
public ResponseEntity<ApiResponse<AuthResponse>> oauth2Success(
        @RequestParam String token) {

    // Decode user info from token
    String email = jwtTokenProvider.getEmailFromToken(token);

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                new ResourceNotFoundException("User not found")
            );

    AuthResponse response = AuthResponse.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();

    return ResponseEntity.ok(
        ApiResponse.success("Google login successful", response)
    );
}
}
