package com.example.miniproject.dto;

import com.example.miniproject.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String city;
    private String country;
    private String avatar;
    private String company;
    private String jobPosition;
    private String mobile;
    private String username;
    private String email;
    private Role role;
}
