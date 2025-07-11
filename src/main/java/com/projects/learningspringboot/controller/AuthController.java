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

//        @PostMapping("/")
//        public ResponseEntity<?> handleAction(@RequestBody Map<String, Object> requestBody) {
//            String action = (String) requestBody.get("action");
//
//            try {
//                if ("register".equalsIgnoreCase(action)) {
//                    RegisterRequest request = mapToRegisterRequest(requestBody);
//                    userService.registerUser(request);
//                    return ResponseEntity.ok(Map.of(
//                            "status", "success",
//                            "message", "User registered. OTP sent to phone."
//                    ));
//                } else if ("userLogin".equalsIgnoreCase(action)) {
//                    LoginRequest request = mapToLoginRequest(requestBody);
//                    return loginUser(request);
//                } else {
//                    return ResponseEntity.badRequest().body(Map.of(
//                            "status", "failure",
//                            "message", "Unsupported action: " + action
//                    ));
//                }
//            } catch (IllegalStateException e) {
//                if ("changepassword".equalsIgnoreCase(e.getMessage())) {
//                    return ResponseEntity.ok(Map.of(
//                            "status", "changepassword",
//                            "username", requestBody.get("username"),
//                            "message", "OTP verified, please change your password"
//                    ));
//                }
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
//                        "status", "failure",
//                        "message", "Invalid credentials"
//                ));
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                        "status", "failure",
//                        "message", "Something went wrong"
//                ));
//            }
//        }

    @PostMapping("/")
    public ResponseEntity<?> handleAction(@RequestBody Map<String, Object> requestBody) {
        String action = (String) requestBody.get("action");

        try {
            switch (action) {
                case "register":
                    RegisterRequest registerRequest = mapToRegisterRequest(requestBody);
                    userService.registerUser(registerRequest);
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "User registered. OTP sent to phone."
                    ));

                case "userLogin":
                    LoginRequest loginRequest = mapToLoginRequest(requestBody);
                    return loginUser(loginRequest);

//                case "fetchAllUsers":
//                    List<User> allUsers = userService.getAllUsers();
//                    return ResponseEntity.ok(allUsers);
//
//                case "fetchUserById":
//                    Integer userId = Integer.parseInt(requestBody.get("id").toString());
//                    User user = userService.getUserById(userId);
//                    return ResponseEntity.ok(user);

                default:
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "failure",
                            "message", "Unsupported action: " + action
                    ));
            }
        } catch (IllegalStateException e) {
            if ("changepassword".equalsIgnoreCase(e.getMessage())) {
                return ResponseEntity.ok(Map.of(
                        "status", "changepassword",
                        "username", requestBody.get("username"),
                        "message", "OTP verified, please change your password"
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "failure",
                    "message", "Invalid credentials"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "failure",
                    "message", "Something went wrong"
            ));
        }
    }





    private ResponseEntity<?> loginUser(LoginRequest request) {
        User user = userService.authenticateUserByUsername(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(user.getUsername(), "ROLE_" + user.getRoleId());

        // Fetch roleName from service or repository using roleId
        String roleName = userService.getRoleNameById(user.getRoleId());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Login successful",
                "sessionToken", token,
                "role", roleName
        ));
    }



    private LoginRequest mapToLoginRequest(Map<String, Object> map) {
            LoginRequest request = new LoginRequest();
            request.setAction((String) map.get("action"));
            request.setUsername((String) map.get("username"));
            request.setPassword((String) map.get("password"));
            return request;
        }

    private RegisterRequest mapToRegisterRequest(Map<String, Object> map) {
        RegisterRequest request = new RegisterRequest();
        request.setAction((String) map.get("action"));
        request.setUsername((String) map.get("username"));
        request.setFirstName((String) map.get("firstName"));
        request.setLastName((String) map.get("lastName"));
        request.setPhoneNumber((String) map.get("phoneNumber"));
        request.setEmail((String) map.get("email"));

        // Fix role handling to always convert to String
        Object role = map.get("role");
        if (role != null) {
            request.setRole(String.valueOf(role));
        }

        return request;
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

    @GetMapping("/")
    public ResponseEntity<?> handleGetActions(@RequestParam String action) {
        if ("fetchAllUsers".equalsIgnoreCase(action)) {
            // Optional: Enforce admin-only restriction internally
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }

        return ResponseEntity.badRequest().body(Map.of(
                "status", "failure",
                "message", "Unsupported action: " + action
        ));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> handleGetUserById(@PathVariable Integer id, @RequestParam String action) {
        if ("fetchUserById".equalsIgnoreCase(action)) {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.badRequest().body(Map.of(
                "status", "failure",
                "message", "Unsupported action: " + action
        ));
    }



//    @GetMapping("/fetchAllUsers")
//    public List<User> getAllUsers() {
//        return userService.getAllUsers();
//    }
//
//    @GetMapping("/users/{id}")
//    public User getUserById(@PathVariable Integer id) {
//        return userService.getUserById(id);
//    }
}
