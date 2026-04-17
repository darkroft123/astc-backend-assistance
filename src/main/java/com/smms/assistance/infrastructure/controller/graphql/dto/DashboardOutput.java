package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardOutput {
    private long totalAttendances;
    private long totalAbsences;
    private long totalJustifications;
    private List<AttendanceOutput> recentAttendances;
    private List<JustificationOutput> recentJustifications;
}
