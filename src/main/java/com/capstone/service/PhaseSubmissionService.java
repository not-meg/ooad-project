package com.capstone.service;

import com.capstone.model.PhaseSubmission;
import com.capstone.repository.PhaseSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PhaseSubmissionService {

    private final PhaseSubmissionRepository phaseSubmissionRepository;

    @Autowired
    public PhaseSubmissionService(PhaseSubmissionRepository phaseSubmissionRepository) {
        this.phaseSubmissionRepository = phaseSubmissionRepository;
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

    public List<PhaseSubmission> getSubmissionsByTeamID(String teamID) {
        return phaseSubmissionRepository.findByTeamID(teamID);
    }    
}