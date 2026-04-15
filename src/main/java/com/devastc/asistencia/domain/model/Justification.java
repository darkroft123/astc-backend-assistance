package com.devastc.asistencia.domain.model;

import com.devastc.asistencia.domain.enumtype.JustificationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class Justification {
    private UUID id;
    private UUID absenceId;
    private UUID userId;
    private String description;
    private String documentUrl;
    private JustificationStatus status;
    private String comment;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private UUID reviewedBy;

    public Justification() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAbsenceId() { return absenceId; }
    public void setAbsenceId(UUID id) { this.absenceId = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID id) { this.userId = id; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String u) { this.documentUrl = u; }
    public JustificationStatus getStatus() { return status; }
    public void setStatus(JustificationStatus s) { this.status = s; }
    public String getComment() { return comment; }
    public void setComment(String c) { this.comment = c; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime d) { this.submittedAt = d; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime d) { this.reviewedAt = d; }
    public UUID getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UUID id) { this.reviewedBy = id; }
}
