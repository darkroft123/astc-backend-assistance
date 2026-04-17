package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterAttendanceInput {
    private String date;
    private String checkIn;
    private String checkOut;
    private UUID projectId;
    private Double latitude;
    private Double longitude;
    private String photoBase64;
    private String photoFilename;
}
