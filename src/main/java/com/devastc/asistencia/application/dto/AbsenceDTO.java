package com.devastc.asistencia.application.dto;

public class AbsenceDTO {
    private String id;
    private String userId;
    private String projectId;
    private String date;
    private String type;
    private boolean justified;

    public AbsenceDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getJustified() {
        return justified;
    }

    public void setJustified(boolean justified) {
        this.justified = justified;
    }
}