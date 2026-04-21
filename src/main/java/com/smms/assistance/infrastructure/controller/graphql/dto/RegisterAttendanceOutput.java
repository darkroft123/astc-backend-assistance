package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterAttendanceOutput {
    private String id;
    private String userId;
    private String date;
    private String checkIn;
    private String status;
}