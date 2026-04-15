package com.devastc.asistencia.application.dto;

public class PaginatedResultDTO {
    private int page;
    private int size;
    private long totalCount;

    public PaginatedResultDTO() {}

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
}
