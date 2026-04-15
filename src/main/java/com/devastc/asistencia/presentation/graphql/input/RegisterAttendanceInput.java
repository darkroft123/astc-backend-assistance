package com.devastc.asistencia.presentation.graphql.input;

import org.eclipse.microprofile.graphql.Input;

@Input("RegisterAttendanceInput")
public class RegisterAttendanceInput {
    private String projectId;
    private Double latitude;
    private Double longitude;
    private String photoUrl;

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
