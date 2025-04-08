package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Faculty extends User {
    private String department;
    private String designation; // ✅ New field

    public Faculty() {
    }

    // Updated constructor
    public Faculty(String userID, String name, String email, String password, String department, String designation) {
        super(userID, name, email, password);
        this.department = department;
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String getRole() {
        return "FACULTY";
    }
}
