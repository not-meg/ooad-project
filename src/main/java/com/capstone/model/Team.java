package com.capstone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "teams")
public class Team {
    @Id
    private String teamID;
    private String problemStatement;
    private String facultyID;
    private Set<String> studentIDs; // Ensuring uniqueness
    private String password; // New field
    private String status = "pending";

    public Team() {
        this.studentIDs = new HashSet<>(); // Enforce unique student IDs
    }

    public Team(String teamID, String problemStatement, String facultyID, String password) {
        this.teamID = teamID;
        this.problemStatement = problemStatement;
        this.facultyID = facultyID;
        this.studentIDs = new HashSet<>();
        this.password = password;
        this.status = "pending";
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(String facultyID) {
        this.facultyID = facultyID;
    }

    public Set<String> getStudentIDs() {
        return studentIDs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean addMember(String studentID) {
        if (studentIDs.size() < 4) {
            return studentIDs.add(studentID);
        }
        return false; // Team already has 4 members
    }

    public boolean removeMember(String studentID) {
        return studentIDs.remove(studentID);
    }

    public boolean updateProblemStatement(String newStatement) {
        if (newStatement != null && !newStatement.isEmpty()) {
            this.problemStatement = newStatement;
            return true;
        }
        return false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
