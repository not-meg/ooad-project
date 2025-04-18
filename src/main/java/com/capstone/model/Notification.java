package com.capstone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;
import java.util.List;

@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Field("team_id")
    private String teamId;
    
    private String faculty_id;
    private List<String> panel_members_id;
    private String title;
    private String comments;
    private String additional_info;
    private Date created_at;
    private Date expire_at;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getFaculty_id() {
        return faculty_id;
    }

    public void setFaculty_id(String faculty_id) {
        this.faculty_id = faculty_id;
    }

    public List<String> getPanel_members_id() {
        return panel_members_id;
    }

    public void setPanel_members_id(List<String> panel_members_id) {
        this.panel_members_id = panel_members_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAdditional_info() {
        return additional_info;
    }

    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getExpire_at() {
        return expire_at;
    }

    public void setExpire_at(Date expire_at) {
        this.expire_at = expire_at;
    }
}
