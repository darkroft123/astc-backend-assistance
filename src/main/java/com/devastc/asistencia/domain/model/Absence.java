package com.devastc.asistencia.domain.model;

import com.devastc.asistencia.domain.enumtype.AbsenceType;
import java.time.LocalDate;
import java.util.UUID;

public class Absence {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private LocalDate date;
    private AbsenceType type;
    private boolean justified;

    public Absence() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID id) { this.userId = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID id) { this.projectId = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate d) { this.date = d; }
    public AbsenceType getType() { return type; }
    public void setType(AbsenceType t) { this.type = t; }
    public boolean isJustified() { return justified; }
    public void setJustified(boolean j) { this.justified = j; }
}
