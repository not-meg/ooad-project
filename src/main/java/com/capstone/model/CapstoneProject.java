package com.capstone.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "capstone_projects") // MongoDB Collection
public class CapstoneProject {
    @Id
    private String id;
    private String title;
    private String description;
    private String teamLead;
    private int teamSize;
}
