package com.projects.learningspringboot;

import com.projects.learningspringboot.model.User;
import com.projects.learningspringboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_success() throws Exception {
        String userJson = """
        {
            "username": "new_user",
            "email": "new@example.com",
            "password": "mypassword"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_duplicateEmail_shouldFail() throws Exception {
        userRepository.save(new User(null,"existing", "dup@example.com", "pass123", "ROLE_USER"));

        String userJson = """
        {
            "username": "another_user",
            "email": "dup@example.com",
            "password": "pass123"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict()); // assuming you handle duplicate as 409
    }
}

