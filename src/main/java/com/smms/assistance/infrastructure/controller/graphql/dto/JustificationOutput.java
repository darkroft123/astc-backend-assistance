package com.smms.assistance.infrastructure.controller.graphql.dto;

import io.smallrye.graphql.api.federation.FieldSet;
import io.smallrye.graphql.api.federation.Key;
import lombok.*;
import java.util.UUID;

@Key(fields = @FieldSet("id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JustificationOutput {
    private UUID id;
    private UUID absenceId;
    private UUID userId;
    private String description;
    private String documentUrl;
    private String status;
    private String comment;
    private String submittedAt;
    private String reviewedAt;
    private UUID reviewedBy;
}