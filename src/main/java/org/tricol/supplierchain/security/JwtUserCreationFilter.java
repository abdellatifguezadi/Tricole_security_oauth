package org.tricol.supplierchain.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.repository.UserRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtUserCreationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || path.startsWith("/oauth2/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String issuer = jwt.getClaimAsString("iss");
            
            if (issuer != null && issuer.contains("realms")) {
                String username = jwt.getClaimAsString("preferred_username");
                
                if (username != null && !userRepository.existsByUsername(username)) {
                    UserApp user = UserApp.builder()
                            .username(username)
                            .email(jwt.getClaimAsString("email"))
                            .fullName(jwt.getClaimAsString("name"))
                            .password("")
                            .enabled(true)
                            .locked(false)
                            .build();
                    userRepository.save(user);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}