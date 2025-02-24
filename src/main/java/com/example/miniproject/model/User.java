package com.example.miniproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be ISO2 format")
    private String country;

    @NotBlank(message = "Avatar URL is required")
    private String avatar;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Job position is required")
    private String jobPosition;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 10, message = "Password must be between 6 and 10 characters")
    private String password;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;
}
