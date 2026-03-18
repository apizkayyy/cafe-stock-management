package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.LoginRequest;
import com.cafe.stockmanagement.dto.request.RegisterRequest;
import com.cafe.stockmanagement.dto.response.AuthResponse;
import com.cafe.stockmanagement.dto.response.UserResponse;
import com.cafe.stockmanagement.entity.User;
import com.cafe.stockmanagement.enums.Role;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.UserRepository;
import com.cafe.stockmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new BadRequestException("Email already registered");
    }

    User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ROLE_STAFF)      // ✅ default role
            .isActive(true)
            .build();

    userRepository.save(user);
    String token = jwtTokenProvider.generateToken(user.getEmail());

    return AuthResponse.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
}

    public AuthResponse login(LoginRequest request) {

        // AuthenticationManager verifies email + password automatically
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // If we reach here, credentials are valid — generate token
        String token = jwtTokenProvider.generateToken(authentication);

        // Get user details for response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

public UserResponse promoteToAdmin(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + userId)
            );

    user.setRole(Role.ROLE_ADMIN);
    userRepository.save(user);

    return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
}
}