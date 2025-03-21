package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Admin extends User {
    public Admin() {}

    public Admin(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
