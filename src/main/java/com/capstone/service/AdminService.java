package com.capstone.service;

import com.capstone.model.Admin;
import com.capstone.model.Student;
import com.capstone.model.Faculty;
import com.capstone.model.User;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    // Get Admin by ID
    public Optional<Admin> getAdminById(String adminId) {
        Optional<User> userOpt = userRepository.findByUserID(adminId);
        if (userOpt.isPresent() && userOpt.get() instanceof Admin) {
            return Optional.of((Admin) userOpt.get());
        }
        return Optional.empty();
    }

    // ✅ Get all users (Admin, Faculty, Student)
    public List<User> getAllUsers() {
        return userRepository.findAll(); // No filtering here
    }

    // Get all Students only
    public List<Student> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Student)
                .map(user -> (Student) user)
                .collect(Collectors.toList());
    }

    // Get all Faculty only
    public List<Faculty> getAllFaculty() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Faculty)
                .map(user -> (Faculty) user)
                .collect(Collectors.toList());
    }

    // Optional: Get all Admins if needed
    public List<Admin> getAllAdmins() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Admin)
                .map(user -> (Admin) user)
                .collect(Collectors.toList());
    }
}
