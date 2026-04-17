package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JustificationStatusOutput {
    private UUID id;
    private String status;
    private String comment;
    private String reviewedAt;
    private UUID reviewedBy;
}
