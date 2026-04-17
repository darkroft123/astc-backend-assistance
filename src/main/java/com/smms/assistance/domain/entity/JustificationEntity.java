package com.smms.assistance.domain.entity;

import com.smms.assistance.domain.model.JustificationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "justifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JustificationEntity {

    @Id
    private UUID id;

    @Column(name = "absence_id", nullable = false)
    private UUID absenceId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String description;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JustificationStatus status;

    private String comment;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
