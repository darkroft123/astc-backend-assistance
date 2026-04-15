package com.devastc.asistencia.application.command;

public class JustifyAbsenceCommand {
    private String absenceId;
    private String userId;

    public String getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(String absenceId) {
        this.absenceId = absenceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}