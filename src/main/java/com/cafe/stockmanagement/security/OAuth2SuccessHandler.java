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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email          = oAuth2User.getAttribute("email");
        String name           = oAuth2User.getAttribute("name");
        String googleId       = oAuth2User.getAttribute("sub");
        String profilePicture = oAuth2User.getAttribute("picture");

        // Find or create user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                    User.builder()
                        .email(email)
                        .name(name)
                        .googleId(googleId)
                        .profilePicture(profilePicture)
                        .role(Role.ROLE_STAFF)
                        .isActive(true)
                        .build()
                ));

        // Generate JWT
        String token = jwtTokenProvider.generateToken(email);

        // ✅ Redirect to frontend callback with all user info
        String redirectUrl = "http://localhost:5173/oauth2/callback"
                + "?token=" + token
                + "&name="  + URLEncoder.encode(user.getName(),  StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
                + "&role="  + user.getRole().name();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}