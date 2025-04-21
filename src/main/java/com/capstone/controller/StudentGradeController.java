package com.capstone.controller;

import com.capstone.model.StudentGrade;
import com.capstone.service.StudentGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Factory interface
interface GradeFactory {
    StudentGrade createGrade(StudentGrade gradeData);
}

// Concrete factory implementation
class StandardGradeFactory implements GradeFactory {
    @Override
    public StudentGrade createGrade(StudentGrade gradeData) {
        // Create a new StudentGrade instance with data from the request
        // This implementation doesn't rely on specific getters/setters
        // but just returns a processed version of the grade
        
        // If you need to perform validations or transformations
        // you can do so here without modifying the original object
        
        // For now, we'll just return the object as is
        // since we don't know the actual structure of your StudentGrade class
        return gradeData;
    }
}

@RestController
@RequestMapping("/api/grades")
public class StudentGradeController {
   
    private final StudentGradeService studentGradeService;
    private final GradeFactory gradeFactory;
   
    // Constructor Injection
    @Autowired
    public StudentGradeController(StudentGradeService studentGradeService) {
        this.studentGradeService = studentGradeService;
        this.gradeFactory = new StandardGradeFactory(); // Default implementation
    }
   
    @PostMapping("/save")
    public ResponseEntity<StudentGrade> saveGrade(@RequestBody StudentGrade gradeRequest) {
        // Use factory to create appropriate grade type
        StudentGrade grade = gradeFactory.createGrade(gradeRequest);
        StudentGrade savedGrade = studentGradeService.saveGrade(grade);
        return ResponseEntity.ok(savedGrade);
    }
   
    @PutMapping("/update/{id}")
    public ResponseEntity<StudentGrade> updateGrade(@PathVariable String id, @RequestBody StudentGrade updatedGrade) {
        StudentGrade grade = gradeFactory.createGrade(updatedGrade);
        StudentGrade result = studentGradeService.updateGrade(id, grade);
        return ResponseEntity.ok(result);
    }
   
    @GetMapping("/team/{teamId}/phase/{phase}")
    public ResponseEntity<List<StudentGrade>> getGradesByTeamAndPhase(@PathVariable String teamId, @PathVariable int phase) {
        List<StudentGrade> grades = studentGradeService.getGradesByTeamIdAndPhase(teamId, phase);
        return ResponseEntity.ok(grades);
    }
   
    @GetMapping("/student/{studentId}/team/{teamId}/phase/{phase}")
    public ResponseEntity<StudentGrade> getGradeByStudentTeamPhase(
            @PathVariable String studentId,
            @PathVariable String teamId,
            @PathVariable int phase) {
        Optional<StudentGrade> grade = studentGradeService.getGradeByStudentTeamPhase(studentId, teamId, phase);
        return grade.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}