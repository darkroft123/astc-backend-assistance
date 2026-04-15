package com.devastc.asistencia.presentation.graphql.output;

import org.eclipse.microprofile.graphql.Type;

@Type("DashboardOutput")
public class DashboardOutput {
    private long totalAttendances;
    private long totalAbsences;
    private long pendingJustifications;

    public long getTotalAttendances() {
        return totalAttendances;
    }

    public void setTotalAttendances(long totalAttendances) {
        this.totalAttendances = totalAttendances;
    }

    public long getTotalAbsences() {
        return totalAbsences;
    }

    public void setTotalAbsences(long totalAbsences) {
        this.totalAbsences = totalAbsences;
    }

    public long getPendingJustifications() {
        return pendingJustifications;
    }

    public void setPendingJustifications(long pendingJustifications) {
        this.pendingJustifications = pendingJustifications;
    }
}
