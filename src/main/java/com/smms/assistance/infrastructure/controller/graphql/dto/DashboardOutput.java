package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardOutput {
    private Integer totalAttendances;
    private Integer totalAbsences;
    private Integer pendingJustifications;
}
