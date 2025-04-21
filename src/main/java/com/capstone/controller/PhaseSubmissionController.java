package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.PhaseSubmissionService;
import com.capstone.service.TeamService;
import com.capstone.service.DriveUploader;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class PhaseSubmissionController {

    private final PhaseSubmissionService submissionService;
    private final TeamService teamService;

    public PhaseSubmissionController(PhaseSubmissionService submissionService, TeamService teamService) {
        this.submissionService = submissionService;
        this.teamService = teamService;

    }

    public String submitPhase(String studentID, File selectedFile, String selectedPhase, StringBuilder resultLog) {
        if (selectedFile == null || selectedPhase == null) {
            return "Please select both a file and a phase.";
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(studentID);
        if (!teamOpt.isPresent()) {
            return "Team not found. Cannot submit.";
        }

        Team team = teamOpt.get();
        String teamId = team.getTeamID();
        int selectedPhaseInt = mapPhaseToInt(selectedPhase);

        if (selectedPhaseInt < 1 || selectedPhaseInt > 4) {
            return "Invalid phase selected.";
        }

        // Validate phase submission order
        String validationResult = validatePhaseSubmission(teamId, selectedPhaseInt);
        if (validationResult != null) {
            return validationResult;
        }

        // Upload to Drive
        String fileId = DriveUploader.uploadFile(selectedFile);
        if (fileId == null) {
            return "❌ Upload failed. Please try again.";
        }

        // Save submission info
        PhaseSubmission submission = new PhaseSubmission(teamId, selectedPhaseInt, fileId);
        boolean dbSuccess = submissionService.saveOrUpdateSubmission(submission);

        if (!dbSuccess) {
            return "❌ Failed to save submission.";
        }

        resultLog.append("✅ Successfully uploaded for phase: ").append(selectedPhase).append("\n");

        // Simulate AI and Plagiarism checks
        simulateAIChecks(submission, resultLog);

        return null; // No error
    }

    private String validatePhaseSubmission(String teamId, int selectedPhaseInt) {
        List<PhaseSubmission> existingSubs = submissionService.getSubmissionsByTeamID(teamId);
        existingSubs.sort(Comparator.comparingInt(PhaseSubmission::getPhase));

        if (!existingSubs.isEmpty()) {
            PhaseSubmission last = existingSubs.get(existingSubs.size() - 1);
            int lastPhase = last.getPhase();
            String lastStatus = last.getStatus();

            if ("Rejected".equalsIgnoreCase(lastStatus)) {
                if (selectedPhaseInt != lastPhase) {
                    return "You must re-submit Phase " + lastPhase + " before proceeding.";
                }
            } else {
                if (lastPhase == 4) {
                    return "✅ All 4 phases already submitted!";
                }
                if (selectedPhaseInt != lastPhase + 1) {
                    return "You must complete Phase " + (lastPhase + 1) + " first.";
                }
            }
        } else {
            if (selectedPhaseInt != 1) {
                return "You must start with Phase 1.";
            }
        }
        return null; // Valid phase submission
    }

    private void simulateAIChecks(PhaseSubmission submission, StringBuilder resultLog) {
        Random rand = new Random();
        int aiScore = rand.nextInt(18);
        int plagiarismScore = rand.nextInt(18);

        resultLog.append("🔍 AI Detection Score: ").append(aiScore).append("%\n");
        resultLog.append("📄 Plagiarism Score: ").append(plagiarismScore).append("%\n");

        if (aiScore > 15 || plagiarismScore > 15) {
            submission.setStatus("Rejected");
            resultLog.append("❌ Submission Rejected: High AI or Plagiarism score.");
        } else {
            submission.setStatus("AI Check Passed");
            resultLog.append("🎉 Submission Accepted!");
        }

        submissionService.updateSubmission(submission);
    }

    private int mapPhaseToInt(String phase) {
        return switch (phase) {
            case "Abstract" -> 1;
            case "Report" -> 2;
            case "Presentation" -> 3;
            case "Final Code" -> 4;
            default -> 0;
        };
    }
}
