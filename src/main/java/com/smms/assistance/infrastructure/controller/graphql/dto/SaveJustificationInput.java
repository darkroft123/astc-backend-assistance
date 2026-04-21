package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveJustificationInput {
    private UUID absenceId;
    private String description;
    private String documentUrl;
}
