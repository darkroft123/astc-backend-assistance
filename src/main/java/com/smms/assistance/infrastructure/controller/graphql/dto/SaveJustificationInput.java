package com.smms.assistance.infrastructure.controller.graphql.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.graphql.Input;
import java.util.UUID;

@Input
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveJustificationInput {

    @NotNull(message = "El ID de la inasistencia es obligatorio.")
    private UUID absenceId;

    @Size(max = 1000, message = "El motivo de la justificación no puede exceder los 1000 caracteres.")
    private String description;

    @Size(max = 10485760, message = "El documento o URL excede el tamaño máximo permitido.")
    private String documentUrl;

    private String fileBase64;
}