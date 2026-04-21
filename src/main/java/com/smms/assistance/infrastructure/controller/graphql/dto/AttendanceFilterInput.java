package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceFilterInput {
    private int page = 0;
    private int size = 20;
    private UUID projectId;
    private String fromDate;
    private String toDate;
    private String status;
}