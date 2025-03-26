package com.capstone;

import com.capstone.model.Student;
import com.capstone.model.User;
import com.capstone.service.TeamService;
import com.capstone.service.UserService;
import com.capstone.repository.UserRepository;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class CapstoneApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;  // Using UserRepository instead of StudentRepository

    public static void main(String[] args) {
        // Start Spring Boot in a separate thread
        Thread springThread = new Thread(() -> SpringApplication.run(CapstoneApplication.class, args));
        springThread.setDaemon(true);
        springThread.start();

        // Start JavaFX UI
        Application.launch(HomepageView.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Fetching and validating student details...");

        // Define student IDs for team registration
        List<String> studentIDs = Arrays.asList("PES1UG22CS001", "PES1UG22CS002", "PES1UG22EC003", "PES1UG22EE004");

        // Validate students
        for (String studentID : studentIDs) {
            Optional<User> userOpt = userRepository.findById(studentID);

            if (userOpt.isPresent() && userOpt.get() instanceof Student) {
                Student student = (Student) userOpt.get();
                System.out.println("✅ Student found: " + student.getName() +
                        " | CGPA: " + student.getCgpa() +
                        " | Gender: " + student.getGender() +
                        " | Department: " + student.getDepartment());
            } else {
                System.out.println("❌ Error: Student with ID " + studentID + " not found or not a student.");
                return; // Stop execution if any student is missing
            }
        }

        System.out.println("Registering team...");

        // Attempt to register team
        boolean success = teamService.registerTeam(
            "T001",
            "AI-powered plagiarism detection",
            "F001",  // Faculty ID
            studentIDs
        );

        System.out.println("Team registration successful? " + success);
    }
}
