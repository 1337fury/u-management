package com.example.miniproject.controller;

import com.example.miniproject.dto.BatchImportResponse;
import com.example.miniproject.dto.UserResponse;
import com.example.miniproject.model.Role;
import com.example.miniproject.model.User;
import com.example.miniproject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/generate")
    @Operation(summary = "Generate random users", description = "Generates a specified number of random users and returns them as a JSON file")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid count parameter")
    })
    public void generateUsers(
            @Parameter(description = "Number of users to generate", required = true)
            @RequestParam @Min(1) int count,
            HttpServletResponse response) throws Exception {
        
        List<User> users = userService.generateUsers(count);
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.json");
        
        objectMapper.writeValue(response.getOutputStream(), users);
    }

    @PostMapping("/batch")
    @Operation(summary = "Import users in batch", description = "Upload a JSON file containing user data for batch import")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Import summary",
            content = @Content(schema = @Schema(implementation = BatchImportResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid file or file content")
    })
    public ResponseEntity<BatchImportResponse> batchImport(
            @Parameter(description = "JSON file containing user data", required = true)
            @RequestParam("file") MultipartFile file) throws Exception {
        
        List<User> users = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        
        return ResponseEntity.ok(userService.importUsers(users));
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Retrieve the profile of the currently authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserByUsername(userDetails.getUsername()));
    }

    @GetMapping("/{username}")
    @Operation(
        summary = "Get user profile by username",
        description = "Retrieve a user's profile by their username (requires ADMIN role)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires admin role"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}
