package com.cafe.stockmanagement.controller;

import com.cafe.stockmanagement.dto.response.ApiResponse;
import com.cafe.stockmanagement.dto.response.UserResponse;
import com.cafe.stockmanagement.enums.Role;
import com.cafe.stockmanagement.entity.User;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // GET all users — Admin only
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    // UPDATE user role — Admin only
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable Long id,
            @RequestParam Role role) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(
            ApiResponse.success("Role updated to " + role.name(), mapToResponse(user))
        );
    }

    // DEACTIVATE user — Admin only
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
    }

    private UserResponse mapToResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .isActive(u.getIsActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}