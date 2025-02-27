package com.example.miniproject.service;

import com.example.miniproject.mapper.UserMapper;
import com.example.miniproject.model.Role;
import com.example.miniproject.model.User;
import com.example.miniproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9. ()-]{10,}$");
    private static final Pattern ISO_COUNTRY_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("Should generate the requested number of users")
    void shouldGenerateRequestedNumberOfUsers() {
        // when
        int count = 5;
        List<User> users = userService.generateUsers(count);

        // then
        assertEquals(count, users.size());
    }

    @Test
    @DisplayName("Should generate users with valid data")
    void shouldGenerateUsersWithValidData() {
        // when
        List<User> users = userService.generateUsers(1);
        User user = users.get(0);

        // then
        assertNotNull(user);
        
        // Check required fields are present
        assertNotNull(user.getFirstName(), "First name should not be null");
        assertNotNull(user.getLastName(), "Last name should not be null");
        assertNotNull(user.getBirthDate(), "Birth date should not be null");
        assertNotNull(user.getCity(), "City should not be null");
        assertNotNull(user.getCountry(), "Country should not be null");
        assertNotNull(user.getAvatar(), "Avatar should not be null");
        assertNotNull(user.getCompany(), "Company should not be null");
        assertNotNull(user.getJobPosition(), "Job position should not be null");
        assertNotNull(user.getMobile(), "Mobile should not be null");
        assertNotNull(user.getUsername(), "Username should not be null");
        assertNotNull(user.getEmail(), "Email should not be null");
        assertNotNull(user.getPassword(), "Password should not be null");
        assertNotNull(user.getRole(), "Role should not be null");

        // Validate formats
        assertTrue(EMAIL_PATTERN.matcher(user.getEmail()).matches(), 
            "Email should be in valid format");
        assertTrue(PHONE_PATTERN.matcher(user.getMobile()).matches(), 
            "Mobile number should be in valid format");
        assertTrue(ISO_COUNTRY_CODE_PATTERN.matcher(user.getCountry()).matches(), 
            "Country should be an ISO2 code");
        assertTrue(user.getPassword().length() >= 6 && user.getPassword().length() <= 10, 
            "Password should be between 6 and 10 characters");
        assertTrue(user.getRole() == Role.ADMIN || user.getRole() == Role.USER, 
            "Role should be either ADMIN or USER");
    }

    @Test
    @DisplayName("Should generate unique users")
    void shouldGenerateUniqueUsers() {
        // when
        int count = 10;
        List<User> users = userService.generateUsers(count);

        // then
        // Check uniqueness of usernames and emails
        long uniqueUsernames = users.stream().map(User::getUsername).distinct().count();
        long uniqueEmails = users.stream().map(User::getEmail).distinct().count();

        assertEquals(count, uniqueUsernames, "All usernames should be unique");
        assertEquals(count, uniqueEmails, "All emails should be unique");
    }

    @Test
    @DisplayName("Should generate users with valid age range (18-65 years)")
    void shouldGenerateUsersWithValidAgeRange() {
        // when
        List<User> users = userService.generateUsers(5);

        // then
        users.forEach(user -> {
            assertNotNull(user.getBirthDate(), "Birth date should not be null");
            // Users should be between 18 and 65 years old
            assertTrue(user.getBirthDate().isBefore(java.time.LocalDate.now().minusYears(18)),
                "User should be at least 18 years old");
            assertTrue(user.getBirthDate().isAfter(java.time.LocalDate.now().minusYears(65)),
                "User should be less than 65 years old");
        });
    }
}
