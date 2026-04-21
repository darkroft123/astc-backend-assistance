package com.smms.assistance.infrastructure.controller.graphql.dto;

import io.smallrye.graphql.api.federation.FieldSet;
import io.smallrye.graphql.api.federation.Key;
import lombok.*;
import java.util.UUID;

@Key(fields = @FieldSet("id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbsenceOutput {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String date;
    private String type;
    private boolean justified;
}