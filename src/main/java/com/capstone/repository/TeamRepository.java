package com.capstone.repository;

import com.capstone.model.Team;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends MongoRepository<Team, String> {
    List<Team> findByFacultyID(String facultyID);
    Optional<Team> findByStudentIDsContaining(String studentID);

}
