package com.devastc.asistencia.application.query;

public class ListJustificationsQuery {
    private String userId;
    private String statusFilter;
    private String projectId;
    private String fromDate;
    private String toDate;
    private int page;
    private int size;

    public ListJustificationsQuery() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStatusFilter() { return statusFilter; }
    public void setStatusFilter(String statusFilter) { this.statusFilter = statusFilter; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }
    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
