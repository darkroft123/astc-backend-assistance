package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.UUID;
import org.eclipse.microprofile.graphql.Input;

@Input("RegisterAttendanceInput")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterAttendanceInput {
    private UUID projectId;
    private Double latitude;
    private Double longitude;
    private String photoUrl;
}