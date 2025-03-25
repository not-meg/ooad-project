package com.capstone;

import com.capstone.service.TeamService;
import com.capstone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class CapstoneApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;  // Inject TeamService

    public static void main(String[] args) {
        SpringApplication.run(CapstoneApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.createTestUser(); // Existing method

        System.out.println("Running team registration test...");

        boolean success = teamService.registerTeam(
            "T001",
            "AI-powered plagiarism detection",
            "F001",  // Faculty ID
            Arrays.asList("PES1UG22CS001", "PES1UG22CS002", "PES1UG22EC003", "PES1UG22EE004")  // Student IDs
        );

        System.out.println("Team registration successful? " + success);
    }
}
