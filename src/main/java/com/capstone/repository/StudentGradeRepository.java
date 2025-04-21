package com.capstone.repository;

import com.capstone.model.StudentGrade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGradeRepository extends MongoRepository<StudentGrade, String> {
    List<StudentGrade> findByStudentId(String studentId);
    List<StudentGrade> findByTeamId(String teamId);
    List<StudentGrade> findByTeamIdAndPhase(String teamId, int phase);
    Optional<StudentGrade> findByStudentIdAndTeamIdAndPhase(String studentId, String teamId, int phase);
    List<StudentGrade> findByPhase(int phase);
}