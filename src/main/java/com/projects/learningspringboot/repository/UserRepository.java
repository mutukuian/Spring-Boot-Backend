package com.projects.learningspringboot.repository;

import com.projects.learningspringboot.model.authmodel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("SELECT r.roleName FROM Role r WHERE r.roleID = :roleId")
    String findRoleNameById(@Param("roleId") int roleId);

}