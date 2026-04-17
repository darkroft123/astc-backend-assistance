package com.smms.assistance.domain.model;

import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardSummary {
    private long totalAttendances;
    private long totalAbsences;
    private long totalJustifications;
    private List<AttendanceEntity> recentAttendances;
    private List<JustificationEntity> recentJustifications;
}
