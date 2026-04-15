package com.devastc.asistencia.application.command;

public class SaveJustificationCommand {
    private String absenceId;
    private String userId;
    private String description;
    private String documentUrl;

    public SaveJustificationCommand() {}

    public String getAbsenceId() { return absenceId; }
    public void setAbsenceId(String absenceId) { this.absenceId = absenceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
}
