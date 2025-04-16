package com.capstone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "notifications")
public class Notification {

    @Id
    @Indexed(unique = true)  // Enforces unique notification IDs in MongoDB
    private String notificationId;

    private String teamId;
    private String reviewDate;
    private String reviewTime;
    private String facultyId;
    private List<String> panelMemberIds;
    private String comments;
    private String additionalInfo;
    private String createdAt;
    private String expireAt;

    public Notification() {}

    public Notification(String notificationId, String teamId, String reviewDate, String reviewTime,
                        String facultyId, List<String> panelMemberIds, String comments, 
                        String additionalInfo, String createdAt, String expireAt) {
        this.notificationId = notificationId;
        this.teamId = teamId;
        this.reviewDate = reviewDate;
        this.reviewTime = reviewTime;
        this.facultyId = facultyId;
        this.panelMemberIds = panelMemberIds;
        this.comments = comments;
        this.additionalInfo = additionalInfo;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
    }

    // Getters
    public String getNotificationId() { return notificationId; }
    public String getTeamId() { return teamId; }
    public String getReviewDate() { return reviewDate; }
    public String getReviewTime() { return reviewTime; }
    public String getFacultyId() { return facultyId; }
    public List<String> getPanelMemberIds() { return panelMemberIds; }
    public String getComments() { return comments; }
    public String getAdditionalInfo() { return additionalInfo; }
    public String getCreatedAt() { return createdAt; }
    public String getExpireAt() { return expireAt; }

    // Setters
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
    public void setReviewTime(String reviewTime) { this.reviewTime = reviewTime; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
    public void setPanelMemberIds(List<String> panelMemberIds) { this.panelMemberIds = panelMemberIds; }
    public void setComments(String comments) { this.comments = comments; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setExpireAt(String expireAt) { this.expireAt = expireAt; }
}
