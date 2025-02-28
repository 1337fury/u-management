package com.example.miniproject.mapper;

import com.example.miniproject.dto.UserResponse;
import com.example.miniproject.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .city(user.getCity())
                .country(user.getCountry())
                .avatar(user.getAvatar())
                .company(user.getCompany())
                .jobPosition(user.getJobPosition())
                .mobile(user.getMobile())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
