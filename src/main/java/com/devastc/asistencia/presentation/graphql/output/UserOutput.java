package com.devastc.asistencia.presentation.graphql.output;

import org.eclipse.microprofile.graphql.Type;

@Type("UserOutput")
public class UserOutput {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String dni;
    private String role;
    private boolean blocked;
    private String assignedProjectId;
    private String registrationDate;
    private String lastLogin;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean getBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public String getAssignedProjectId() { return assignedProjectId; }
    public void setAssignedProjectId(String assignedProjectId) { this.assignedProjectId = assignedProjectId; }
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}
