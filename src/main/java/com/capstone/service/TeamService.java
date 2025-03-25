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
    private UserRepository userRepository;  // To validate students and faculty

    public boolean registerTeam(String teamID, String problemStatement, String facultyID, List<String> studentIDs) {
        if (studentIDs.size() != 4) {
            System.out.println("❌ Team must have exactly 4 unique students.");
            return false;
        }

        Set<String> uniqueStudents = new HashSet<>(studentIDs);
        if (uniqueStudents.size() < 4) {
            System.out.println("❌ Duplicate students found! Each student must be unique.");
            return false;
        }

        // Fetch student details from the database
        Set<Student> students = new HashSet<>();
        for (String studentID : studentIDs) {
            Optional<User> userOpt = userRepository.findByUserID(studentID);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Student)) {
                System.out.println("❌ Student with ID " + studentID + " does not exist or is not a student.");
                return false;
            }
            students.add((Student) userOpt.get());
        }

        // Fetch faculty details from the database
        Optional<User> facultyOpt = userRepository.findByUserID(facultyID);
        if (facultyOpt.isEmpty() || !(facultyOpt.get() instanceof Faculty)) {
            System.out.println("❌ Faculty with ID " + facultyID + " does not exist or is not a faculty.");
            return false;
        }
        Faculty faculty = (Faculty) facultyOpt.get();

        // Ensure faculty’s department matches at least one student’s department
        boolean departmentMatch = students.stream()
                                          .anyMatch(student -> student.getDepartment().equals(faculty.getDepartment()));

        if (!departmentMatch) {
            System.out.println("❌ Faculty's department does not match any student’s department.");
            return false;
        }

        // ✅ If all checks pass, create and save the team
        Team team = new Team(teamID, problemStatement, faculty.getUserID());
        studentIDs.forEach(team::addMember);
        teamRepository.save(team);

        System.out.println("✅ Team registered successfully!");
        return true;
    }
}
