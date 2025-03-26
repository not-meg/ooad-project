package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.repository.PhaseSubmissionRepository;
import com.capstone.service.PhaseSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/phase-submissions")
public class PhaseSubmissionController {

    @Autowired
    private PhaseSubmissionService phaseSubmissionService;

    @PostMapping("/submit")
    public PhaseSubmission submitDocument(@RequestParam String teamID,
                                          @RequestParam int phase,
                                          @RequestParam String documentPath) {
        return phaseSubmissionService.submitDocument(teamID, phase, documentPath);
    }

    @GetMapping("/team/{teamID}")
    public List<PhaseSubmission> getSubmissionsByTeam(@PathVariable String teamID) {
        return phaseSubmissionService.getSubmissionsByTeam(teamID);
    }

    @PostMapping("/grade")
    public boolean gradeSubmission(@RequestParam String submissionID,
                                   @RequestParam float grade,
                                   @RequestParam String feedback) {
        return phaseSubmissionService.gradeSubmission(submissionID, grade, feedback);
    }
}

