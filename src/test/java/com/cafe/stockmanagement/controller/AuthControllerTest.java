package com.cafe.stockmanagement.controller;

import com.cafe.stockmanagement.dto.request.LoginRequest;
import com.cafe.stockmanagement.dto.request.RegisterRequest;
import com.cafe.stockmanagement.dto.response.AuthResponse;
import com.cafe.stockmanagement.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ Create directly — no need to autowire
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register - Should register successfully")
    void register_ValidRequest_Returns200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Hafiz Test");
        request.setEmail("hafiz@test.com");
        request.setPassword("password123");

        AuthResponse mockResponse = AuthResponse.builder()
                .token("mock.jwt.token")
                .name("Hafiz Test")
                .email("hafiz@test.com")
                .role("ROLE_STAFF")
                .build();

        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.data.email").value("hafiz@test.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should fail with invalid email")
    void register_InvalidEmail_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Hafiz Test");
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should login successfully")
    void login_ValidCredentials_Returns200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("hafiz@test.com");
        request.setPassword("password123");

        AuthResponse mockResponse = AuthResponse.builder()
                .token("mock.jwt.token")
                .name("Hafiz Test")
                .email("hafiz@test.com")
                .role("ROLE_STAFF")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock.jwt.token"));
    }
}