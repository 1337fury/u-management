package com.example.miniproject.controller;

import com.example.miniproject.dto.UserResponse;
import com.example.miniproject.model.Role;
import com.example.miniproject.security.JwtTokenProvider;
import com.example.miniproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnCurrentUserProfile() throws Exception {
        // Arrange
        UserResponse mockResponse = createMockUserResponse("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/users/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()); // Either 401 Unauthorized or 403 Forbidden
    }

    private UserResponse createMockUserResponse(String username) {
        return UserResponse.builder()
            .username(username)
            .firstName("Test")
            .lastName("User")
            .email("test@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .city("Test City")
            .country("US")
            .company("Test Company")
            .jobPosition("Test Position")
            .mobile("+1234567890")
            .role(Role.USER)
            .avatar("https://example.com/avatar.jpg")
            .build();
    }
}
