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

    public String authenticateUser(String userID, String teamID, String password) {
        Optional<User> userOpt = userRepository.findById(userID);
    
        if (userOpt.isEmpty()) {
            return "INVALID"; // User does not exist
        }
    
        User user = userOpt.get();
    
        if (user instanceof Student) {
            if (teamID == null || teamID.isBlank()) {
                return "MISSING_TEAM"; // Students must provide a team ID
            }
            if (!teamService.teamExists(teamID) || !teamService.validateTeamPassword(teamID, password)) {
                return "INVALID"; // Invalid team or incorrect team password
            }
            return "STUDENT"; // Authentication successful as a Student
        } 
        else { // If user is Faculty
            if (!user.getPassword().equals(password)) {
                return "INVALID"; // Incorrect faculty password
            }
            return "FACULTY"; // Authentication successful as a Faculty
        }
    }    
}

