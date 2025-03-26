package com.capstone;

import com.capstone.model.User;
import com.capstone.model.Student;
import com.capstone.model.PhaseSubmission;
import com.capstone.service.TeamService;
import com.capstone.service.PhaseSubmissionService;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class CapstoneApplication implements CommandLineRunner {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhaseSubmissionService phaseSubmissionService;

    public static void main(String[] args) {
        SpringApplication.run(CapstoneApplication.class, args); // ✅ Start Spring Boot
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your user ID (Student ID): ");
        String studentID = scanner.nextLine();

        // Fetch user from DB
        Optional<User> userOpt = userRepository.findById(studentID);
        if (userOpt.isEmpty() || !(userOpt.get() instanceof Student)) {
            System.out.println("❌ Invalid user. Only students can log in.");
            return;
        }

        System.out.println("Enter your Team ID: ");
        String teamID = scanner.nextLine();

        // Validate team existence
        if (!teamService.teamExists(teamID)) {
            System.out.println("❌ Team does not exist.");
            return;
        }

        System.out.println("Enter team password: ");
        String teamPassword = scanner.nextLine();

        // Validate team password
        if (!teamService.validateTeamPassword(teamID, teamPassword)) {
            System.out.println("❌ Invalid team password. Try again.");
            return;
        }

        System.out.println("✅ Login successful!");

        System.out.println("Enter phase number: ");
        int phase = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter document path (e.g., /uploads/report.pdf): ");
        String documentPath = scanner.nextLine();

        // Submit Phase
        PhaseSubmission submission = new PhaseSubmission(teamID, phase, documentPath);
        phaseSubmissionService.submitPhase(submission);

        System.out.println("✅ Phase " + phase + " submitted successfully for Team " + teamID);
    }
}
