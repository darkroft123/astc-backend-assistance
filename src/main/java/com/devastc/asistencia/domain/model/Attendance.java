package com.devastc.asistencia.domain.model;

import com.devastc.asistencia.domain.enumtype.AttendanceStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Attendance {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private AttendanceStatus status;
    private Double latitude;
    private Double longitude;
    private String photoUrl;

    public Attendance() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID id) { this.userId = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID id) { this.projectId = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate d) { this.date = d; }
    public LocalTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalTime t) { this.checkIn = t; }
    public LocalTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalTime t) { this.checkOut = t; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus s) { this.status = s; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double l) { this.latitude = l; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double l) { this.longitude = l; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String u) { this.photoUrl = u; }
}
