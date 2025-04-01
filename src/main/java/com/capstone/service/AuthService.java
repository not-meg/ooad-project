package com.capstone.service;

import com.capstone.model.Student;
import com.capstone.model.User;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamService teamService;

    public boolean authenticateUser(String userID, String teamID, String password) {
        Optional<User> userOpt = userRepository.findById(userID);

        if (userOpt.isEmpty()) {
            return false; // User does not exist
        }

        User user = userOpt.get();

        // Case 1: If user is a Student, they MUST provide a team ID and password should be validated from teams
        if (user instanceof Student) {
            if (teamID == null || teamID.isBlank()) {
                return false; // Students must provide a valid team ID
            }
            if (!teamService.teamExists(teamID) || !teamService.validateTeamPassword(teamID, password)) {
                return false; // Invalid team or incorrect team password
            }
        }
        // Case 2: If user is a Faculty, they can log in without a team ID, validate password from user collection
        else {
            if (!user.getPassword().equals(password)) {
                return false; // Incorrect faculty password
            }
        }

        return true; // Authentication successful
    }
}

