package com.cafe.stockmanagement.security;

import com.cafe.stockmanagement.entity.User;
import com.cafe.stockmanagement.enums.Role;
import com.cafe.stockmanagement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // Step 1 — Get user info from Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email         = oAuth2User.getAttribute("email");
        String name          = oAuth2User.getAttribute("name");
        String googleId      = oAuth2User.getAttribute("sub");   // Google's unique user ID
        String picture       = oAuth2User.getAttribute("picture");

        // Step 2 — Find or create user in our database
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // First time Google login → create new user
                    User newUser = User.builder()
                            .name(name)
                            .email(email)
                            .googleId(googleId)
                            .profilePicture(picture)
                            .role(Role.ROLE_STAFF)
                            .isActive(true)
                            .build();
                    return userRepository.save(newUser);
                });

        // Step 3 — Update Google info if user already exists
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            user.setProfilePicture(picture);
            userRepository.save(user);
        }

        // Step 4 — Generate our JWT token
        String token = jwtTokenProvider.generateToken(email);

        // Step 5 — Redirect with token in URL
        // In production, redirect to your frontend with the token
        String redirectUrl = "http://localhost:8089/api/auth/oauth2/success?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}