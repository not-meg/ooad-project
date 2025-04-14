package com.capstone.service;

import com.capstone.model.PhaseSubmission;
import com.capstone.repository.PhaseSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PhaseSubmissionService {

    private final PhaseSubmissionRepository phaseSubmissionRepository;

    @Autowired
    public PhaseSubmissionService(PhaseSubmissionRepository phaseSubmissionRepository) {
        this.phaseSubmissionRepository = phaseSubmissionRepository;
    }

    public boolean uploadSubmission(String phase, File file) {
        try {
            // Simulate file upload to Google Drive (or local storage)
            String fileUrl = DriveUploader.uploadFile(file);  // This method would upload to Google Drive and return the URL

            // Create a new PhaseSubmission document
            PhaseSubmission submission = new PhaseSubmission();
            submission.setTeamID("T001");  // Replace with actual team ID
            submission.setPhase(mapPhaseStringToNumber(phase));  // Map phase string to integer
            submission.setDocumentPath(fileUrl);  // Save the URL/path of the uploaded file
            submission.setSubmissionDate(java.time.LocalDateTime.now());

            // Save the PhaseSubmission document in MongoDB
            phaseSubmissionRepository.save(submission);

            System.out.println("Successfully uploaded file: " + file.getName() + " for phase: " + phase);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int mapPhaseStringToNumber(String phase) {
        switch (phase) {
            case "Abstract":
                return 1;
            case "Report":
                return 2;
            case "Presentation":
                return 3;
            case "Final Code":
                return 4;
            default:
                return -1; // Invalid phase
        }
    }

    public boolean saveSubmission(PhaseSubmission submission) {
        try {
            phaseSubmissionRepository.save(submission);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
