package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.LoginRequest;
import com.cafe.stockmanagement.dto.request.RegisterRequest;
import com.cafe.stockmanagement.dto.response.AuthResponse;
import com.cafe.stockmanagement.entity.User;
import com.cafe.stockmanagement.enums.Role;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.repository.UserRepository;
import com.cafe.stockmanagement.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)         // Use Mockito with JUnit 5
@DisplayName("AuthService Tests")
class AuthServiceTest {

    // @Mock creates a fake version of each dependency
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;

    // @InjectMocks creates the real AuthService and injects the mocks above
    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach             // Runs before EACH test method
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Hafiz Test");
        registerRequest.setEmail("hafiz@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("hafiz@test.com");
        loginRequest.setPassword("password123");

        mockUser = User.builder()
                .name("Hafiz Test")
                .email("hafiz@test.com")
                .password("encodedPassword")
                .role(Role.ROLE_STAFF)
                .isActive(true)
                .build();
    }

    // ✅ TEST 1 — Register success
    @Test
    @DisplayName("Should register user successfully")
    void register_Success() {
        // ARRANGE — set up what mocks should return
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("mock.jwt.token");

        // ACT — call the method we're testing
        AuthResponse response = authService.register(registerRequest);

        // ASSERT — verify the result
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("hafiz@test.com");
        assertThat(response.getRole()).isEqualTo("ROLE_STAFF");

        // Verify save was called exactly once
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ✅ TEST 2 — Register fails when email exists
    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_EmailAlreadyExists_ThrowsException() {
        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // ACT & ASSERT — expect this exception to be thrown
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already registered");

        // Verify save was NEVER called
        verify(userRepository, never()).save(any(User.class));
    }

    // ✅ TEST 3 — Login success
    @Test
    @DisplayName("Should login successfully with correct credentials")
    void login_Success() {
        // ARRANGE
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class))
        ).thenReturn(mockAuth);
        when(jwtTokenProvider.generateToken(any(Authentication.class)))
            .thenReturn("mock.jwt.token");
        when(userRepository.findByEmail(anyString()))
            .thenReturn(Optional.of(mockUser));

        // ACT
        AuthResponse response = authService.login(loginRequest);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getName()).isEqualTo("Hafiz Test");
    }
}