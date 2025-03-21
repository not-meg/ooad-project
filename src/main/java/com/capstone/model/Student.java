package com.capstone.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Student extends User {
    private String department;
    private Gender gender;  // Added gender enum field
    private double cgpa;    // Added CGPA field

    public Student() {}

    public Student(String userID, String name, String email, String password, String department, Gender gender, double cgpa) {
        super(userID, name, email, password);
        this.department = department;
        this.gender = gender;
        this.cgpa = cgpa;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    @Override
    public String getRole() {
        return "STUDENT";
    }
}
