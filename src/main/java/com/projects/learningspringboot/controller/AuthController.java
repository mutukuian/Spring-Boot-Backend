package com.projects.learningspringboot.controller;

import com.projects.learningspringboot.model.authmodel.User;
import com.projects.learningspringboot.model.dto.ChangePasswordRequest;
import com.projects.learningspringboot.model.dto.JwtResponse;
import com.projects.learningspringboot.model.dto.LoginRequest;
import com.projects.learningspringboot.model.dto.RegisterRequest;
import com.projects.learningspringboot.security.JwtUtil;
import com.projects.learningspringboot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered. OTP sent to phone.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUserByUsername(request.getUsername(), request.getPassword());

            System.out.println("Authenticated user: " + user.getUsername() + ", role: " + user.getRoleId());

            String token = jwtUtil.generateToken(user.getUsername(), "ROLE_" + user.getRoleId());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (IllegalStateException e) {
            if (e.getMessage().equalsIgnoreCase("changepassword")) {
                // Let the frontend know to redirect user to change password page
                return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                        "status", "changepassword",
                        "username", request.getUsername(),
                        "message", "OTP verified, please change your password"
                ));
            }
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            System.out.println("Unexpected error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            String result = userService.changePassword(request.getUsername(), request.getNewPassword());

            // Authenticate again and return JWT
            User user = userService.authenticateUserByUsername(request.getUsername(), request.getNewPassword());
            String token = jwtUtil.generateToken(user.getUsername(), "ROLE_" + user.getRoleId());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }



    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }
}
