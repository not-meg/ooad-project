package com.capstone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "reviews") // Ensure the collection name matches
public class Review {

    @Id
    private String id;
    private String teamId;
    private String facultyId;
    private List<String> panelMembersId;
    private String status;
    private int phase; // Directly store the integer phase
    private String title;
    private LocalDate reviewDate;
    private String reviewTime;

    // Getters
    public String getId() { return id; }
    public String getTeamId() { return teamId; }
    public String getFacultyId() { return facultyId; }
    public List<String> getPanelMembersId() { return panelMembersId; }
    public String getStatus() { return status; }
    public int getPhase() { return phase; }
    public String getTitle() { return title; }
    public LocalDate getReviewDate() { return reviewDate; }
    public String getReviewTime() { return reviewTime; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
    public void setPanelMembersId(List<String> panelMembersId) { this.panelMembersId = panelMembersId; }
    public void setStatus(String status) { this.status = status; }
    public void setPhase(int phase) { this.phase = phase; }
    public void setTitle(String title) { this.title = title; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
    public void setReviewTime(String reviewTime) { this.reviewTime = reviewTime; }
}