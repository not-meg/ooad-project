package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Faculty extends User {
    private String department;

    public Faculty() {}

    public Faculty(String userID, String name, String email, String password, String department) {
        super(userID, name, email, password);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getRole() {
        return "FACULTY";
    }
}
