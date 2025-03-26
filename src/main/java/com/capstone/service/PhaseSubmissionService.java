package com.capstone.service;

import com.capstone.model.PhaseSubmission;
import com.capstone.repository.PhaseSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhaseSubmissionService {

    @Autowired
    private PhaseSubmissionRepository phaseSubmissionRepository;

    /**
     * Submits a document for a given phase.
     * Ensures no duplicate submission exists for the same team and phase.
     */
    public PhaseSubmission submitDocument(String teamID, int phase, String documentPath) {
        // Check if a submission already exists for this phase
        List<PhaseSubmission> existingSubmissions = phaseSubmissionRepository.findByTeamID(teamID);
        for (PhaseSubmission submission : existingSubmissions) {
            if (submission.getPhase() == phase) {
                System.out.println("⚠️ Error: A submission for Phase " + phase + " already exists.");
                return null;
            }
        }

        PhaseSubmission submission = new PhaseSubmission(teamID, phase, documentPath);
        return phaseSubmissionRepository.save(submission);
    }

    /**
     * Alternative method to submit a phase submission (fixing missing method issue).
     * This allows submitting a full PhaseSubmission object instead of individual fields.
     */
    public PhaseSubmission submitPhase(PhaseSubmission submission) {
        return phaseSubmissionRepository.save(submission);
    }

    /**
     * Fetches all submissions for a given team.
     */
    public List<PhaseSubmission> getSubmissionsByTeam(String teamID) {
        return phaseSubmissionRepository.findByTeamID(teamID);
    }

    /**
     * Fetches a specific submission by ID.
     */
    public Optional<PhaseSubmission> getSubmissionById(String submissionID) {
        return phaseSubmissionRepository.findById(submissionID);
    }

    /**
     * Grades a submission by updating its grade and feedback.
     * Prevents overwriting an existing grade.
     */
    public boolean gradeSubmission(String submissionID, float grade, String feedback) {
        Optional<PhaseSubmission> submissionOpt = phaseSubmissionRepository.findById(submissionID);
        
        if (submissionOpt.isPresent()) {
            PhaseSubmission submission = submissionOpt.get();

            // Prevent overwriting an already graded submission
            if (submission.getGrade() != null) {
                System.out.println("⚠️ Error: Submission has already been graded.");
                return false;
            }

            submission.setGrade(grade);
            submission.setFeedback(feedback);
            phaseSubmissionRepository.save(submission);
            System.out.println("✅ Submission graded successfully!");
            return true;
        } 
        
        System.out.println("❌ Error: Submission not found.");
        return false;
    }
}
