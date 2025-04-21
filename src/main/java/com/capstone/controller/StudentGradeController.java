package com.capstone.controller;

import com.capstone.model.StudentGrade;
import com.capstone.service.StudentGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grades")
public class StudentGradeController {

    @Autowired
    private StudentGradeService studentGradeService;

    @PostMapping("/save")
    public StudentGrade saveGrade(@RequestBody StudentGrade grade) {
        return studentGradeService.saveGrade(grade);
    }

    @PutMapping("/update/{id}")
    public StudentGrade updateGrade(@PathVariable String id, @RequestBody StudentGrade updatedGrade) {
        return studentGradeService.updateGrade(id, updatedGrade);
    }

    @GetMapping("/team/{teamId}/phase/{phase}")
    public List<StudentGrade> getGradesByTeamAndPhase(@PathVariable String teamId, @PathVariable int phase) {
        return studentGradeService.getGradesByTeamIdAndPhase(teamId, phase);
    }

    @GetMapping("/student/{studentId}/team/{teamId}/phase/{phase}")
    public Optional<StudentGrade> getGradeByStudentTeamPhase(@PathVariable String studentId,
                                                              @PathVariable String teamId,
                                                              @PathVariable int phase) {
        return studentGradeService.getGradeByStudentTeamPhase(studentId, teamId, phase);
    }
}
