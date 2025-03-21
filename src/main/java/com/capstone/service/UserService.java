package com.capstone.service;

import com.capstone.model.*;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void createTestUser() {
        // Insert a student (UserID starts with "S")
        insertUser(new Student("PES1UG22CS001", "Test Student", "student@example.com", "student123", "CS", Gender.MALE, 9.2));

        // Insert a faculty member (UserID starts with "F")
        insertUser(new Faculty("F002", "Test Prof", "test.prof@pes.edu", "faculty123", "CS"));

        // Insert an admin (UserID starts with "A")
        insertUser(new Admin("A001", "Admin User", "admin@example.com", "admin123"));
    }

    private void insertUser(User user) {
        try {
            String userId = user.getUserID();
            if (userId == null || userId.isEmpty()) {
                System.out.println("Invalid User ID! Skipping insertion.");
                return;
            }

            if (userRepository.existsById(userId)) {
                System.out.println("User with ID " + userId + " already exists! Skipping insertion.");
                return;
            }

            userRepository.save(user);
            System.out.println("User with ID " + userId + " saved to MongoDB!");
        } catch (Exception e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
    }
}
