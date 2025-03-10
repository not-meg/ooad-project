package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users") // Stored in the same collection
public class Student extends User {
    private String teamID;
    private String semester;

    public Student(String name, String email, String password, String teamID, String semester) {
        super(name, email, password, "STUDENT");
        this.teamID = teamID;
        this.semester = semester;
    }

    // Getters and Setters
    public String getTeamID() { return teamID; }
    public void setTeamID(String teamID) { this.teamID = teamID; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
