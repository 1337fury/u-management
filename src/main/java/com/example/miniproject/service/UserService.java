package com.example.miniproject.service;

import com.example.miniproject.dto.BatchImportResponse;
import com.example.miniproject.dto.UserResponse;
import com.example.miniproject.exception.ResourceNotFoundException;
import com.example.miniproject.mapper.UserMapper;
import com.example.miniproject.model.Role;
import com.example.miniproject.model.User;
import com.example.miniproject.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Faker faker = new Faker(new Locale("en"));

    /**
     * Generate a specified number of random users
     */
    public List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateRandomUser());
        }
        return users;
    }

    /**
     * Import users in batch, checking for duplicates
     */
    @Transactional
    public BatchImportResponse importUsers(List<User> users) {
        int totalRecords = users.size();
        int successCount = 0;
        int failureCount = 0;

        for (User user : users) {
            try {
                if (!userRepository.existsByEmail(user.getEmail()) 
                    && !userRepository.existsByUsername(user.getUsername())) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                failureCount++;
            }
        }

        return BatchImportResponse.builder()
                .totalRecords(totalRecords)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

    /**
     * Get user by username
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toUserResponse(user);
    }

    /**
     * Find user by username or email (for authentication)
     */
    public User findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + identifier));
    }

    /**
     * Generate a random user using JavaFaker
     */
    private User generateRandomUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(faker.date().past(36500, TimeUnit.DAYS).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .city(faker.address().city())
                .country(faker.address().countryCode())
                .avatar(faker.avatar().image())
                .company(faker.company().name())
                .jobPosition(faker.job().position())
                .mobile(faker.phoneNumber().cellPhone())
                .username(faker.name().username())
                .email(email)
                .password(generateRandomPassword())
                .role(Math.random() < 0.2 ? Role.ADMIN : Role.USER) // 20% chance of being admin
                .build();
    }

    /**
     * Generate a random password between 6 and 10 characters
     */
    private String generateRandomPassword() {
        return faker.regexify("[a-zA-Z0-9]{6,10}");
    }
}
