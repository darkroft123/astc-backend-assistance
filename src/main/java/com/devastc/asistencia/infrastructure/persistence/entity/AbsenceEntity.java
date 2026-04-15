package com.devastc.asistencia.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "absences")
public class AbsenceEntity {
    @Id private UUID id;
    @Column(name = "user_id") private UUID userId;
    @Column(name = "project_id") private UUID projectId;
    private LocalDate date;
    private String type;
    private boolean justified;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID v) { this.userId = v; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID v) { this.projectId = v; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate v) { this.date = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public boolean isJustified() { return justified; }
    public void setJustified(boolean v) { this.justified = v; }
}
