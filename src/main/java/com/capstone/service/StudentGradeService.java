package com.capstone.service;

import com.capstone.model.StudentGrade;
import com.capstone.repository.StudentGradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class StudentGradeService {

    @Autowired
    private StudentGradeRepository studentGradeRepository;

    public List<StudentGrade> getAllGrades() {
        return studentGradeRepository.findAll();
    }

    public Optional<StudentGrade> getGradeById(String id) {
        return studentGradeRepository.findById(id);
    }

    public List<StudentGrade> getGradesByStudentId(String studentId) {
        return studentGradeRepository.findByStudentId(studentId);
    }

    public List<StudentGrade> getGradesByTeamId(String teamId) {
        return studentGradeRepository.findByTeamId(teamId);
    }

    public List<StudentGrade> getGradesByTeamIdAndPhase(String teamId, int phase) {
        return studentGradeRepository.findByTeamIdAndPhase(teamId, phase);
    }

    public Optional<StudentGrade> getGradeByStudentTeamPhase(String studentId, String teamId, int phase) {
        return studentGradeRepository.findByStudentIdAndTeamIdAndPhase(studentId, teamId, phase);
    }

    public List<StudentGrade> getGradesByPhase(int phase) {
        return studentGradeRepository.findByPhase(phase);
    }

    public StudentGrade saveGrade(StudentGrade studentGrade) {
        studentGrade.setLastUpdated(Instant.now());
        return studentGradeRepository.save(studentGrade);
    }

    public StudentGrade updateGrade(String id, StudentGrade updatedGrade) {
        Optional<StudentGrade> existingGradeOptional = studentGradeRepository.findById(id);
        
        if (existingGradeOptional.isPresent()) {
            StudentGrade existingGrade = existingGradeOptional.get();
            
            // Update fields
            if (updatedGrade.getStudentId() != null) {
                existingGrade.setStudentId(updatedGrade.getStudentId());
            }
            if (updatedGrade.getTeamId() != null) {
                existingGrade.setTeamId(updatedGrade.getTeamId());
            }
            if (updatedGrade.getPhase() > 0) {
                existingGrade.setPhase(updatedGrade.getPhase());
            }
            if (updatedGrade.getIsa1Grade() != null) {
                existingGrade.setIsa1Grade(updatedGrade.getIsa1Grade());
            }
            if (updatedGrade.getIsa2Grade() != null) {
                existingGrade.setIsa2Grade(updatedGrade.getIsa2Grade());
            }
            if (updatedGrade.getEsaGrade() != null) {
                existingGrade.setEsaGrade(updatedGrade.getEsaGrade());
            }
            
            existingGrade.updateLastModified();
            return studentGradeRepository.save(existingGrade);
        } else {
            throw new IllegalArgumentException("Grade with ID " + id + " not found");
        }
    }

    public StudentGrade updateIsa1Grade(String id, String isa1Grade) {
        Optional<StudentGrade> gradeOptional = studentGradeRepository.findById(id);
        if (gradeOptional.isPresent()) {
            StudentGrade grade = gradeOptional.get();
            grade.setIsa1Grade(isa1Grade);
            return studentGradeRepository.save(grade);
        } else {
            throw new IllegalArgumentException("Grade with ID " + id + " not found");
        }
    }

    public StudentGrade updateIsa2Grade(String id, String isa2Grade) {
        Optional<StudentGrade> gradeOptional = studentGradeRepository.findById(id);
        if (gradeOptional.isPresent()) {
            StudentGrade grade = gradeOptional.get();
            grade.setIsa2Grade(isa2Grade);
            return studentGradeRepository.save(grade);
        } else {
            throw new IllegalArgumentException("Grade with ID " + id + " not found");
        }
    }

    public StudentGrade updateEsaGrade(String id, String esaGrade) {
        Optional<StudentGrade> gradeOptional = studentGradeRepository.findById(id);
        if (gradeOptional.isPresent()) {
            StudentGrade grade = gradeOptional.get();
            grade.setEsaGrade(esaGrade);
            return studentGradeRepository.save(grade);
        } else {
            throw new IllegalArgumentException("Grade with ID " + id + " not found");
        }
    }

    public void deleteGrade(String id) {
        studentGradeRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return studentGradeRepository.existsById(id);
    }

    public long count() {
        return studentGradeRepository.count();
    }
}