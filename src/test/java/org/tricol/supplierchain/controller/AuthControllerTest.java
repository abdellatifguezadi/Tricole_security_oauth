package org.tricol.supplierchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.exception.GlobalHandler;
import org.tricol.supplierchain.service.inter.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - Authentification")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalHandler())
                .build();
        objectMapper = new ObjectMapper();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        authResponse = AuthResponse.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .role("MAGASINIER")
                .accessToken("valid_token")
                .tokenType("Bearer")
                .build();
    }

    @Test
    @DisplayName("Test 1: testGetAccessTokenFail - Échec avec identifiants incorrects")
    void testGetAccessTokenFail() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("wronguser");
        invalidRequest.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    @DisplayName("Test 2: testAccessTokenSuccess - Succès avec identifiants corrects")
    void testAccessTokenSuccess() throws Exception {
        when(authService.login(any(LoginRequest.class), any(HttpServletResponse.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("valid_token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}