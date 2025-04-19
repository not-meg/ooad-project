package com.capstone.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.List;

public class Review {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty teamId = new SimpleStringProperty();
    private final StringProperty facultyId = new SimpleStringProperty();
    private final ListProperty<String> panelMembersId = new SimpleListProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    private final IntegerProperty phase = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> reviewDate = new SimpleObjectProperty<>();
    private final StringProperty reviewTime = new SimpleStringProperty();

    public Review() {}

    // Getters and setters with properties
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }
    public StringProperty idProperty() { return id; }

    public String getTeamId() { return teamId.get(); }
    public void setTeamId(String teamId) { this.teamId.set(teamId); }
    public StringProperty teamIdProperty() { return teamId; }

    public String getFacultyId() { return facultyId.get(); }
    public void setFacultyId(String facultyId) { this.facultyId.set(facultyId); }
    public StringProperty facultyIdProperty() { return facultyId; }

    public List<String> getPanelMembersId() { return panelMembersId.get(); }
    public void setPanelMembersId(List<String> panelMembersId) { this.panelMembersId.setAll(panelMembersId); }
    public ListProperty<String> panelMembersIdProperty() { return panelMembersId; }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    public int getPhase() { return phase.get(); }
    public void setPhase(int phase) { this.phase.set(phase); }
    public IntegerProperty phaseProperty() { return phase; }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public LocalDate getReviewDate() { return reviewDate.get(); }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate.set(reviewDate); }
    public ObjectProperty<LocalDate> reviewDateProperty() { return reviewDate; }

    public String getReviewTime() { return reviewTime.get(); }
    public void setReviewTime(String reviewTime) { this.reviewTime.set(reviewTime); }
    public StringProperty reviewTimeProperty() { return reviewTime; }
}
