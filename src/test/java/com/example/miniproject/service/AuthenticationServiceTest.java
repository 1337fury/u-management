package com.example.miniproject.service;

import com.example.miniproject.dto.AuthRequest;
import com.example.miniproject.dto.AuthResponse;
import com.example.miniproject.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_TOKEN = "test.jwt.token";

    @Test
    void shouldAuthenticateValidUserAndReturnToken() {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(TEST_TOKEN);

        // Act
        AuthResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getAccessToken());
    }

    @Test
    void shouldThrowExceptionForInvalidCredentials() {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_USERNAME, "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(request);
        });
    }
}
