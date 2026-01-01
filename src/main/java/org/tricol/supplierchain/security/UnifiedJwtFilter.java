package org.tricol.supplierchain.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.io.IOException;
import java.util.*;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UnifiedJwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        
        try {
            if (isKeycloakToken(jwt)) {
                Jwt keycloakJwt = decodeKeycloakToken(jwt);
                String username = keycloakJwt.getClaimAsString("preferred_username");
                
                Collection<GrantedAuthority> authorities = new HashSet<>();
                try {
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
                    authorities.addAll(customUserDetails.getAuthorities());
                } catch (Exception e) {
                }
                
                authorities.addAll(extractKeycloakAuthorities(keycloakJwt));
                
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(keycloakJwt, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                final String username = jwtService.extractUsername(jwt);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isKeycloakToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                return payload.contains("tricol-realm") || payload.contains("localhost:8180");
            }
        } catch (Exception ignored) {
        }
        return false;
    }
    
    private Jwt decodeKeycloakToken(String token) {
        JwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri("http://localhost:8180/realms/tricol-realm/protocol/openid-connect/certs")
                .build();
        return decoder.decode(token);
    }
    
    private Collection<GrantedAuthority> extractKeycloakAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        try {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error extracting Keycloak roles: " + e.getMessage());
        }
        
        return authorities;
    }
}