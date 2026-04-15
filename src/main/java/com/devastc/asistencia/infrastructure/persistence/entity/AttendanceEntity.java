package com.devastc.asistencia.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "attendances")
public class AttendanceEntity {
    @Id private UUID id;
    @Column(name = "user_id") private UUID userId;
    @Column(name = "project_id") private UUID projectId;
    private LocalDate date;
    @Column(name = "check_in") private LocalTime checkIn;
    @Column(name = "check_out") private LocalTime checkOut;
    private String status;
    private Double latitude;
    private Double longitude;
    @Column(name = "photo_url") private String photoUrl;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID v) { this.userId = v; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID v) { this.projectId = v; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate v) { this.date = v; }
    public LocalTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalTime v) { this.checkIn = v; }
    public LocalTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalTime v) { this.checkOut = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double v) { this.latitude = v; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double v) { this.longitude = v; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String v) { this.photoUrl = v; }
}
