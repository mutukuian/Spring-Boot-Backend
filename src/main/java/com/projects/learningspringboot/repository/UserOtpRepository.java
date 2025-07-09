package com.projects.learningspringboot.repository;

import com.projects.learningspringboot.model.authmodel.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpRepository extends JpaRepository<UserOtp, Integer> {

    // Get latest OTP for the user
    Optional<UserOtp> findTopByUserIdOrderByCreatedAtDesc(Integer userId);

    // Optional: if you want to fetch by username (not usually needed with userId available)
    Optional<UserOtp> findTopByUsernameOrderByCreatedAtDesc(String username);
}
