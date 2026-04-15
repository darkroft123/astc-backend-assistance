package com.devastc.asistencia.presentation.graphql.input;

import org.eclipse.microprofile.graphql.Input;

@Input("SaveJustificationInput")
public class SaveJustificationInput {
    private String absenceId;
    private String description;
    private String documentUrl;

    public String getAbsenceId() { return absenceId; }
    public void setAbsenceId(String absenceId) { this.absenceId = absenceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
}
