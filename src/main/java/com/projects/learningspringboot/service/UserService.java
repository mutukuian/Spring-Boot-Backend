package com.projects.learningspringboot.service;

import com.projects.learningspringboot.exception.UserNotFoundException;
import com.projects.learningspringboot.model.dto.RegisterRequest;
import com.projects.learningspringboot.model.authmodel.User;
import com.projects.learningspringboot.model.authmodel.UserOtp;
import com.projects.learningspringboot.repository.UserOtpRepository;
import com.projects.learningspringboot.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserOtpRepository  userOtpRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, UserOtpRepository userOtpRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userOtpRepository = userOtpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }

        Integer roleId = switch (request.getRole().toUpperCase()) {
            case "ADMIN" -> 1;
            case "USER" -> 2;
            default -> 2;
        };

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoleId(roleId);
        user.setStatusID(3); // Awaiting OTP verification

        userRepository.save(user);

        String otp = generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        // Save OTP to DB
        UserOtp userOtp = new UserOtp();
        userOtp.setUserId(user.getUserID());
        userOtp.setUsername(user.getUsername());
        userOtp.setOtpCode(hashedOtp);
        userOtp.setStatusId(0);
        userOtpRepository.save(userOtp);

        // Send OTP via SMS (mock or real)
        sendOtpToPhone(user.getPhoneNumber(), otp);
    }

    public void sendOtpToPhone(String phoneNumber, String otp) {
        System.out.println("Sending OTP " + otp + " to " + phoneNumber);
        // Integrate with SMS API provider
    }

    public String generateOtp() {
        return String.valueOf(new Random().nextInt(899999) + 100000); // 6-digit
    }


    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }


//    public User authenticateUserByUsername(String username, String password) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));
//        System.out.println("Raw password entered: " + password);
//        System.out.println("Hashed password from DB: " + user.getPassword());
//
//        if (passwordEncoder.matches(password, user.getPassword())) {
//            System.out.println("Passwords match!");
//            return user;
//        } else {
//            System.out.println("Passwords do NOT match!");
//            throw new IllegalStateException("Invalid credentials");
//        }
//    }

    public User authenticateUserByUsername(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));

        Integer statusId = user.getStatusID();

        if (statusId == 3) {
            // First-time login (OTP) — check OTP from user_otp table
            Optional<UserOtp> userOtpOpt = userOtpRepository.findTopByUserIdOrderByCreatedAtDesc(user.getUserID());
            if (userOtpOpt.isEmpty()) {
                throw new IllegalStateException("OTP not found");
            }

            UserOtp userOtp = userOtpOpt.get();

            // Compare hashed OTP with entered value
            if (passwordEncoder.matches(password, userOtp.getOtpCode())) {
                // Signal frontend to redirect to change password screen
                throw new IllegalStateException("changepassword");
            } else {
                throw new IllegalStateException("Invalid OTP");
            }

        } else if (statusId == 1) {
            // Active account — use regular password authentication
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new IllegalStateException("Invalid credentials");
            }

        } else {
            // Inactive or unknown status
            throw new IllegalStateException("User is not active");
        }
    }


    public String changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Hash and save the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatusID(1); // Set to active
        userRepository.save(user);

        return "Password updated successfully";
    }

}
