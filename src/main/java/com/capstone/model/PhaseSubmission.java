package com.capstone.model;

import com.capstone.model.PhaseSubmission;
//import com.capstone.repository.PhaseSubmissionRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "phase_submissions")
public class PhaseSubmission {
    
    @Id
    private String submissionID;
    private String teamID;
    private int phase;
    private String documentPath; // Store file path or URL
    private LocalDateTime submissionDate;
    private Float grade;
    private String feedback;

    // Constructors
    public PhaseSubmission() {}

    public PhaseSubmission(String teamID, int phase, String documentPath) {
        this.teamID = teamID;
        this.phase = phase;
        this.documentPath = documentPath;
        this.submissionDate = LocalDateTime.now();
        this.grade = null;
        this.feedback = null;
    }

    // Getters and Setters
    public String getSubmissionID() { return submissionID; }
    public String getTeamID() { return teamID; }
    public int getPhase() { return phase; }
    public String getDocumentPath() { return documentPath; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public Float getGrade() { return grade; }
    public String getFeedback() { return feedback; }

    public void setGrade(Float grade) { this.grade = grade; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
