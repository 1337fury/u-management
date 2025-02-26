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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
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
    private void validatePassword(String password, String username) {
        if (password == null || password.length() < 6 || password.length() > 10) {
            throw new IllegalArgumentException("Password for user '" + username + "' must be between 6 and 10 characters");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BatchImportResponse importUsers(List<User> users) {
        int totalRecords = users.size();
        List<User> successfulUsers = new ArrayList<>();
        List<String> failedUsernames = new ArrayList<>();

        // First pass: validate all users
        for (User user : users) {
            try {
                if (userRepository.existsByEmail(user.getEmail())) {
                    failedUsernames.add(user.getUsername());
                    continue;
                }
                if (userRepository.existsByUsername(user.getUsername())) {
                    failedUsernames.add(user.getUsername());
                    continue;
                }
                // Validate password before encoding
                validatePassword(user.getPassword(), user.getUsername());
                // Encode password
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                successfulUsers.add(user);
            } catch (Exception e) {
                failedUsernames.add(user.getUsername());
                log.error("Failed to process user {}: {}", user.getUsername(), e.getMessage());
            }
        }

        // Second pass: save all valid users in a single transaction
        if (!successfulUsers.isEmpty()) {
            userRepository.saveAll(successfulUsers);
        }

        return BatchImportResponse.builder()
                .totalRecords(totalRecords)
                .successCount(successfulUsers.size())
                .failureCount(failedUsernames.size())
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
                .birthDate(faker.date().between(
                        java.util.Date.from(java.time.LocalDate.now().minusYears(65).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        java.util.Date.from(java.time.LocalDate.now().minusYears(18).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    ).toInstant()
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
