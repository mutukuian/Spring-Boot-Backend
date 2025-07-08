package com.projects.learningspringboot.controller;

import com.projects.learningspringboot.model.SoftwareEngineer;
import com.projects.learningspringboot.service.SoftwareEngineerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/software-engineers")
public class SoftwareEngineerController {

    private final SoftwareEngineerService softwareEngineerService;

    public SoftwareEngineerController(SoftwareEngineerService softwareEngineerService) {
        this.softwareEngineerService = softwareEngineerService;
    }

    @GetMapping
    public List<SoftwareEngineer> getSoftwareEngineers() {
        return softwareEngineerService.getAllSoftwareEngineers();
    }

    @GetMapping("{id}")
    public SoftwareEngineer getEngineersById(
            @PathVariable Integer id
    ) {
        return softwareEngineerService.getAllSoftwareEngineersById(id);
    }

    @PostMapping
    public void addNewSoftwareEngineer(@RequestBody SoftwareEngineer softwareEngineer) {
      softwareEngineerService.insertSoftwareEngineer(softwareEngineer);
    }
}
