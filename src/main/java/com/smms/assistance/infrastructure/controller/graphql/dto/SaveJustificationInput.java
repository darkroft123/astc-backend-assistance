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

    @Size(max = 500, message = "El motivo de la justificación no puede exceder los 500 caracteres.")
    private String description;

    @Size(max = 2048, message = "La URL del documento no puede exceder los 2048 caracteres.")
    private String documentUrl;

    private String fileBase64;
}