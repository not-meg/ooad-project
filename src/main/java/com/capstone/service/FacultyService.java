package com.capstone.service;

import com.capstone.model.Faculty;
import com.capstone.model.User;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FacultyService {

    private final UserRepository userRepository;

    @Autowired
    public FacultyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Faculty getFacultyByID(String facultyID) {
        Optional<User> user = userRepository.findByUserID(facultyID);

        // Ensure the user exists and is actually a Faculty instance
        if (user.isPresent() && user.get() instanceof Faculty) {
            return (Faculty) user.get();
        }

        return null; // Return null if user doesn't exist or is not a faculty member
    }
}
