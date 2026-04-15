package com.devastc.asistencia.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "justifications")
public class JustificationEntity {
    @Id private UUID id;
    @Column(name = "absence_id") private UUID absenceId;
    @Column(name = "user_id") private UUID userId;
    private String description;
    @Column(name = "document_url") private String documentUrl;
    private String status;
    private String comment;
    @Column(name = "submitted_at") private LocalDateTime submittedAt;
    @Column(name = "reviewed_at") private LocalDateTime reviewedAt;
    @Column(name = "reviewed_by") private UUID reviewedBy;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public UUID getAbsenceId() { return absenceId; }
    public void setAbsenceId(UUID v) { this.absenceId = v; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID v) { this.userId = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String v) { this.documentUrl = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getComment() { return comment; }
    public void setComment(String v) { this.comment = v; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime v) { this.submittedAt = v; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime v) { this.reviewedAt = v; }
    public UUID getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UUID v) { this.reviewedBy = v; }
}
