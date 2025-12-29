package org.tricol.supplierchain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.security.CustomOAuth2User;
import org.tricol.supplierchain.security.JwtService;
import org.tricol.supplierchain.service.inter.AuthService;

import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        if (oauth2User instanceof CustomOAuth2User customUser) {
            String accessToken = jwtService.generateTokenFromUser(customUser.getUserApp());
            
            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .username(customUser.getUserApp().getUsername())
                    .email(customUser.getUserApp().getEmail())
                    .role(customUser.getUserApp().getRole() != null ? 
                          customUser.getUserApp().getRole().getName().name() : "NO_ROLE")
                    .build();
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.badRequest().body("OAuth2 authentication required. Use: http://localhost:8080/oauth2/authorization/keycloak");
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.badRequest().body("OAuth2 authentication failed");
    }

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> getOAuth2Token(
            @RequestBody Map<String, String> request
    ) {
        try {
            String keycloakTokenUrl = "http://keycloak:8080/realms/tricol-realm/protocol/openid-connect/token";
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", "tricol-client");
            body.add("client_secret", "4I9yaf3JX7Vo4cuYknuum4mC6yfzyEmK");
            body.add("username", request.get("username"));
            body.add("password", request.get("password"));
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> keycloakResponse = restTemplate.postForEntity(keycloakTokenUrl, entity, Map.class);
            
            if (keycloakResponse.getStatusCode().is2xxSuccessful()) {
                // Créer l'utilisateur en base si nécessaire
                authService.createKeycloakUserIfNotExists(request.get("username"));
                
                Map<String, Object> tokenData = keycloakResponse.getBody();
                return ResponseEntity.ok(tokenData);
            }
            
            return ResponseEntity.badRequest().body("Invalid credentials");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoints working! Use /login for JWT or /oauth2/authorization/keycloak for OAuth2");
    }
}

