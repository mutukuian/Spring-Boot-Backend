package com.projects.learningspringboot.authtest;

import com.projects.learningspringboot.model.authmodel.User;
import com.projects.learningspringboot.repository.UserRepository;
import com.projects.learningspringboot.security.JwtUtil;
import com.projects.learningspringboot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;


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


        Exception exception = assertThrows(IllegalStateException.class, () -> userService.registerUser(user2));

        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void loginUser_success() throws Exception {
        User user = new User(null,"john", "john@example.com", passwordEncoder.encode("pass123"), "ROLE_USER");
        userRepository.save(user);

        String loginJson = """
    {
        "username": "john",
        "password": "pass123"
    }
    """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }


    @Test
    void getUsers_adminAccess_success() throws Exception {
        // Register an admin user and get token
        User admin = new User(null,"admin", "admin@email.com", passwordEncoder.encode("adminpass"), "ROLE_ADMIN");
        userRepository.save(admin);

        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        mockMvc.perform(get("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}

