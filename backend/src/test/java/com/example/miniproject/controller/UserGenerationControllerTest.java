package com.example.miniproject.controller;

import com.example.miniproject.model.User;
import com.example.miniproject.security.JwtTokenProvider;
import com.example.miniproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldGenerateUsersAndTriggerFileDownload() throws Exception {
        // Arrange
        int count = 5;
        List<User> mockUsers = Arrays.asList(
            User.builder().username("user1").email("user1@example.com").build(),
            User.builder().username("user2").email("user2@example.com").build(),
            User.builder().username("user3").email("user3@example.com").build(),
            User.builder().username("user4").email("user4@example.com").build(),
            User.builder().username("user5").email("user5@example.com").build()
        );
        
        when(userService.generateUsers(anyInt())).thenReturn(mockUsers);

        // Act
        MvcResult result = mockMvc.perform(get("/api/users/generate")
                .param("count", String.valueOf(count)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        
        // Assert
        MockHttpServletResponse response = result.getResponse();
        String contentDisposition = response.getHeader("Content-Disposition");
        
        assertTrue(contentDisposition.contains("attachment"));
        assertTrue(contentDisposition.contains("filename=users.json"));
        assertTrue(response.getContentAsString().contains("user1"));
        assertTrue(response.getContentAsString().contains("user5"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCount() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/generate")
                .param("count", "0"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status >= 400 && status < 600,
                        "Expected error status (4xx or 5xx) but got " + status);
                });
    }

    @Test
    void shouldReturnBadRequestForMissingCount() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/generate"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status >= 400 && status < 600,
                        "Expected error status (4xx or 5xx) but got " + status);
                });
    }

    @Test
    void shouldReturnBadRequestForNonNumericCount() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/generate")
                .param("count", "abc"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status >= 400 && status < 600,
                        "Expected error status (4xx or 5xx) but got " + status);
                });
    }
}
