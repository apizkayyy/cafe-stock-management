package com.cafe.stockmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = runs exactly once per request

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Extract token from request header
        String token = extractTokenFromRequest(request);

        // Step 2: Validate token
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // Step 3: Get email from token
            String email = jwtTokenProvider.getEmailFromToken(token);

            // Step 4: Load user from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Step 5: Create authentication object
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Step 6: Tell Spring Security "this user is authenticated"
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Step 7: Continue to the next filter/controller
        filterChain.doFilter(request, response);
    }

    // Extract "Bearer <token>" from Authorization header
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
/*

🧠 **The Filter Flow — Every Request:**
```
Request arrives
      ↓
JwtAuthFilter runs
      ↓
Has "Authorization: Bearer <token>" header?
      ↓ YES              ↓ NO
Validate token      Continue as anonymous
      ↓
Extract email
      ↓
Load user from DB
      ↓
Set authentication in SecurityContext
      ↓
Controller runs (knows who you are)
*/