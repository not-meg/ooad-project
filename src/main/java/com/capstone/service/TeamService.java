package com.capstone.service;

import com.capstone.model.User;
import com.capstone.model.Team;
import com.capstone.model.Student;
import com.capstone.model.Faculty;
import com.capstone.repository.TeamRepository;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository; // To validate students and faculty

    // ✅ Fetch Student by SRN (_id)
    public Student getStudentBySRN(String srn) {
        Optional<User> userOpt = userRepository.findById(srn);
        if (userOpt.isPresent() && userOpt.get() instanceof Student) {
            return (Student) userOpt.get();
        }
        return null; // Return null if not found or not a student
    }

    // ✅ NEW: Check if any student is already in another team
    private boolean isStudentInAnyTeam(List<String> studentIDs) {
        for (Team team : teamRepository.findAll()) {
            for (String studentID : studentIDs) {
                if (team.getStudentIDs().contains(studentID)) {
                    System.out.println("❌ Student " + studentID + " is already in another team.");
                    return true; // Found a student in another team
                }
            }
        }
        return false; // All students are available
    }

    public boolean registerTeam(String teamID, String problemStatement, String facultyID, List<String> studentIDs,
            String password) {
        if (studentIDs.size() != 4) {
            System.out.println("❌ Team must have exactly 4 unique students.");
            return false;
        }

        Set<String> uniqueStudents = new HashSet<>(studentIDs);
        if (uniqueStudents.size() < 4) {
            System.out.println("❌ Duplicate students found! Each student must be unique.");
            return false;
        }

        // ✅ Check if any student is already in another team
        if (isStudentInAnyTeam(studentIDs)) {
            System.out.println("❌ One or more students are already in another team.");
            return false;
        }

        // Fetch student details from the database
        Set<Student> students = new HashSet<>();
        for (String studentID : studentIDs) {
            Student student = getStudentBySRN(studentID);
            if (student == null) {
                System.out.println("❌ Student with ID " + studentID + " does not exist or is not a student.");
                return false;
            }
            students.add(student);
        }

        // Fetch faculty details from the database
        Optional<User> facultyOpt = userRepository.findById(facultyID);
        if (facultyOpt.isEmpty() || !(facultyOpt.get() instanceof Faculty)) {
            System.out.println("❌ Faculty with ID " + facultyID + " does not exist or is not a faculty.");
            return false;
        }
        Faculty faculty = (Faculty) facultyOpt.get();

        // Ensure faculty’s department matches at least one student’s department
        boolean departmentMatch = students.stream()
                .anyMatch(student -> faculty.getDepartment().equals(student.getDepartment()));

        if (!departmentMatch) {
            System.out.println("❌ Faculty's department does not match any student's department.");
            return false;
        }
        // ✅ Check how many teams the faculty is already mentoring
        long teamsMentored = teamRepository.findAll().stream()
                .filter(t -> t.getFacultyID().equals(faculty.getUserID()))
                .count();

        int maxTeamsAllowed = switch (faculty.getDesignation().toLowerCase()) {
            case "professor" -> 7;
            case "associate professor" -> 5;
            case "assistant professor" -> 3;
            default -> 0;
        };

        if (teamsMentored >= maxTeamsAllowed) {
            System.out.println("❌ " + faculty.getDesignation() + " can mentor only up to " + maxTeamsAllowed
                    + " teams. Limit reached.");
            return false;
        }

        // ✅ If all checks pass, create and save the team
        Team team = new Team(teamID, problemStatement, faculty.getUserID(), password); // Pass password
        studentIDs.forEach(team::addMember);
        teamRepository.save(team);

        System.out.println("✅ Team registered successfully!");
        return true;
    }

    public boolean teamExists(String teamID) {
        return teamRepository.findById(teamID).isPresent();
    }

    public boolean validateTeamPassword(String teamID, String enteredPassword) {
        Optional<Team> teamOpt = teamRepository.findById(teamID);
        return teamOpt.isPresent() && teamOpt.get().getPassword().equals(enteredPassword);
    }

    public Optional<Team> getTeamByID(String teamID) {
        return teamRepository.findById(teamID);
    }

    public Optional<Team> getTeamByStudentID(String studentID) {
        return teamRepository.findByStudentIDsContaining(studentID);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
}
