package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Faculty extends User {
    private String designation;
    private int maxTeamsAllowed;

    public Faculty(String name, String email, String password, String designation, int maxTeamsAllowed) {
        super(name, email, password, "FACULTY");
        this.designation = designation;
        this.maxTeamsAllowed = maxTeamsAllowed;
    }

    // Getters and Setters
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public int getMaxTeamsAllowed() { return maxTeamsAllowed; }
    public void setMaxTeamsAllowed(int maxTeamsAllowed) { this.maxTeamsAllowed = maxTeamsAllowed; }
}
