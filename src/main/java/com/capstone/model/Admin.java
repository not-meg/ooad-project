package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Admin extends User {
    public Admin(String name, String email, String password) {
        super(name, email, password, "ADMIN");
    }
}
