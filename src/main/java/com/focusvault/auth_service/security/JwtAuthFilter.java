package com.focusvault.auth_service.security;

import com.focusvault.auth_service.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. If no token, skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract token (remove "Bearer " prefix)
        final String token = authHeader.substring(7);

        // 4. Extract email from token
        final String email = jwtService.extractEmail(token);

        // 5. If email found and user not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load user from database
            var userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                var user = userOptional.get();

                // 7. Validate token
                if (jwtService.isTokenValid(token, email)) {

                    // 8. Create authentication object
                    var authToken = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 10. Continue to next filter
        filterChain.doFilter(request, response);
    }
}