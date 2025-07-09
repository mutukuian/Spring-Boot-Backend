package com.projects.learningspringboot.model.authmodel;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_otp")
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otpID")
    private Integer otpId;

    @Column(name = "userID", nullable = false)
    private Integer userId;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "otpCode", nullable = false, length = 250)
    private String otpCode;

    @Column(name = "statusID", nullable = false)
    private Integer statusId = 0;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "expiredAt")
    private Timestamp expiredAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.expiredAt = new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000); // 30 minutes from now
    }

    // Getters and Setters
    public Integer getOtpId() {
        return otpId;
    }

    public void setOtpId(Integer otpId) {
        this.otpId = otpId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }
}
