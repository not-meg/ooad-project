package com.capstone.service;

import com.capstone.model.Faculty;
import com.capstone.model.Team;
import com.capstone.model.User;
import com.capstone.repository.TeamRepository;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public FacultyService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public Faculty getFacultyByID(String facultyID) {
        Optional<User> user = userRepository.findByUserID(facultyID);
        if (user.isPresent() && user.get() instanceof Faculty) {
            return (Faculty) user.get();
        }
        return null;
    }

    public List<Team> getTeamsByFacultyID(String facultyID) {
        return teamRepository.findByFacultyID(facultyID);
    }
}
