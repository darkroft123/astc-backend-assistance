package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JustifyAbsenceInput {
    private UUID absenceId;
}
