package com.capstone.service;

import java.io.File;

public class PhaseSubmissionService {

    public boolean uploadSubmission(String phase, File file) {
        try {
            // Simulated upload logic (e.g., store metadata or push file to backend)
            System.out.println("Uploading file: " + file.getName() + " for phase: " + phase);

            // Later, replace this with MongoDB/Drive/Cloud logic
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
