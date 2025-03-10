package com.capstone.controller;

import com.capstone.model.CapstoneProject;
import com.capstone.service.CapstoneProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class CapstoneProjectController {

    @Autowired
    private CapstoneProjectService service;

    @GetMapping
    public List<CapstoneProject> getAllProjects() {
        return service.getAllProjects();
    }

    @GetMapping("/{id}")
    public Optional<CapstoneProject> getProjectById(@PathVariable String id) {
        return service.getProjectById(id);
    }

    @PostMapping
    public CapstoneProject addProject(@RequestBody CapstoneProject project) {
        return service.addProject(project);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable String id) {
        service.deleteProject(id);
    }
}
