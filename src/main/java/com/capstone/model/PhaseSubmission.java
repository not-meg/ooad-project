package com.capstone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "phase_submissions")
public class PhaseSubmission {

    @Id
    private String submissionID;
    private String teamID;
    private int phase;
    private String documentID; // File path or Drive URL
    private LocalDateTime submissionDate;
    private Float grade;
    private String feedback;

    // Constructors
    public PhaseSubmission() {}

    public PhaseSubmission(String teamID, int phase, String documentID) {
        this.teamID = teamID;
        this.phase = phase;
        this.documentID = documentID;
        this.submissionDate = LocalDateTime.now();
        this.grade = null;
        this.feedback = null;
    }

    // Getters
    public String getSubmissionID() { return submissionID; }
    public String getTeamID() { return teamID; }
    public int getPhase() { return phase; }
    public String getDocumentPath() { return documentID; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public Float getGrade() { return grade; }
    public String getFeedback() { return feedback; }

    // Setters
    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public void setDocumentPath(String documentID) {
        this.documentID = documentID;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
