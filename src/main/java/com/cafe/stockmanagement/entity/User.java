package com.cafe.stockmanagement.entity;

import com.cafe.stockmanagement.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder                    // Lets us do User.builder().name("John").build()
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true)          // No two users with same email
    private String email;

    private String password;        // Nullable — Google OAuth users have no password

    @Enumerated(EnumType.STRING)    // Store "ROLE_ADMIN" not "0" in DB
    @Column(nullable = false)
    private Role role;

    private String googleId;        // Filled only for OAuth2 Google users

    private String profilePicture;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}