package com.capstone.service;

import com.capstone.model.CapstoneProject;
import com.capstone.repository.CapstoneProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CapstoneProjectService {

    @Autowired
    private CapstoneProjectRepository repository;

    public List<CapstoneProject> getAllProjects() {
        return repository.findAll();
    }

    public Optional<CapstoneProject> getProjectById(String id) {
        return repository.findById(id);
    }

    public CapstoneProject addProject(CapstoneProject project) {
        return repository.save(project);
    }

    public void deleteProject(String id) {
        repository.deleteById(id);
    }
}
