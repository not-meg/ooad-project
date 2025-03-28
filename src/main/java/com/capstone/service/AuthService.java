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

    public boolean authenticateUser(String studentID, String teamID, String teamPassword) {
        Optional<User> userOpt = userRepository.findById(studentID);
        if (userOpt.isEmpty() || !(userOpt.get() instanceof Student)) {
            return false; // Invalid user
        }
        if (!teamService.teamExists(teamID) || !teamService.validateTeamPassword(teamID, teamPassword)) {
            return false; // Invalid team or password
        }
        return true; // Authentication successful
    }
}
