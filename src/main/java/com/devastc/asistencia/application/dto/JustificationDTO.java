package com.devastc.asistencia.application.dto;

public class JustificationDTO {
    private String id;
    private String absenceId;
    private String userId;
    private String description;
    private String documentUrl;
    private String status;
    private String comment;
    private String submittedAt;
    private String reviewedAt;

    public JustificationDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAbsenceId() { return absenceId; }
    public void setAbsenceId(String absenceId) { this.absenceId = absenceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }
}
