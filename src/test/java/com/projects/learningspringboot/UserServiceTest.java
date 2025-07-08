package com.projects.learningspringboot;

import com.projects.learningspringboot.model.User;
import com.projects.learningspringboot.repository.UserRepository;
import com.projects.learningspringboot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("securePassword123");

        userService.registerUser(user);

        Optional<User> savedUser = userRepository.findByUsername("john_doe");
        assertTrue(savedUser.isPresent());
        assertTrue(passwordEncoder.matches("securePassword123", savedUser.get().getPassword()));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        User user1 = new User(null, "user1", "john@example.com", "pass123", "ROLE_USER");
        userRepository.save(user1);

        User user2 = new User(null, "user2", "john@example.com", "pass123", "ROLE_USER");


        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userService.registerUser(user2);
        });

        assertEquals("Email already in use", exception.getMessage());
    }
}

