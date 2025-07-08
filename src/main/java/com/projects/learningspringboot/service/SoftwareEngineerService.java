package com.projects.learningspringboot.service;

import com.projects.learningspringboot.model.SoftwareEngineer;
import com.projects.learningspringboot.repository.SoftwareEngineerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoftwareEngineerService {

    private final SoftwareEngineerRepository softwareEngineerRepository;

    public SoftwareEngineerService(SoftwareEngineerRepository softwareEngineerRepository) {
        this.softwareEngineerRepository = softwareEngineerRepository;
    }

    public List<SoftwareEngineer> getAllSoftwareEngineers() {
        return softwareEngineerRepository.findAll();
    }

    public void insertSoftwareEngineer(SoftwareEngineer softwareEngineer) {
        softwareEngineerRepository.save(softwareEngineer);
    }

    public SoftwareEngineer getAllSoftwareEngineersById(Integer id) {
        return softwareEngineerRepository.findById(id).orElseThrow(()-> new IllegalStateException(
                "SoftwareEngineer with id " + id + " not found."
        ));
    }
}
