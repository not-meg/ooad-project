package com.capstone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "student_grade")
public class StudentGrade {
    @Id
    private String id;
    private String studentId;
    private String teamId;
    private int phase;
    private String isa1Grade;
    private String isa2Grade;
    private String esaGrade;
    private Instant lastUpdated;

    public StudentGrade() {
        this.lastUpdated = Instant.now();
    }

    public StudentGrade(String id, String studentId, String teamId, int phase, 
                        String isa1Grade, String isa2Grade, String esaGrade) {
        this.id = id;
        this.studentId = studentId;
        this.teamId = teamId;
        this.phase = phase;
        this.isa1Grade = isa1Grade;
        this.isa2Grade = isa2Grade;
        this.esaGrade = esaGrade;
        this.lastUpdated = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getIsa1Grade() {
        return isa1Grade;
    }

    public void setIsa1Grade(String isa1Grade) {
        this.isa1Grade = isa1Grade;
        this.lastUpdated = Instant.now();
    }

    public String getIsa2Grade() {
        return isa2Grade;
    }

    public void setIsa2Grade(String isa2Grade) {
        this.isa2Grade = isa2Grade;
        this.lastUpdated = Instant.now();
    }

    public String getEsaGrade() {
        return esaGrade;
    }

    public void setEsaGrade(String esaGrade) {
        this.esaGrade = esaGrade;
        this.lastUpdated = Instant.now();
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Helper method to update last modified time
    public void updateLastModified() {
        this.lastUpdated = Instant.now();
    }
}