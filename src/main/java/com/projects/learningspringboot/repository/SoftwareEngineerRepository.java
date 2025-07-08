package com.projects.learningspringboot.repository;

import com.projects.learningspringboot.model.SoftwareEngineer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftwareEngineerRepository
        extends JpaRepository<SoftwareEngineer,Integer> {
}
