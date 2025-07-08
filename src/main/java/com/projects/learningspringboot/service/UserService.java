package com.projects.learningspringboot.service;

import com.projects.learningspringboot.exception.UserNotFoundException;
import com.projects.learningspringboot.model.User;
import com.projects.learningspringboot.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void registerUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }

        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER"; // Default role
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }


    public User authenticateUserByUsername(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));
        System.out.println("🔐 Raw password entered: " + password);
        System.out.println("🧾 Hashed password from DB: " + user.getPassword());

        if (passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("✅ Passwords match!");
            return user;
        } else {
            System.out.println("❌ Passwords do NOT match!");
            throw new IllegalStateException("Invalid credentials");
        }
    }

}
