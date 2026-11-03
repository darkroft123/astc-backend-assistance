package com.smms.assistance.infrastructure.controller.graphql.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.graphql.Input;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Input("RegisterAttendanceInput")
public class RegisterAttendanceInput {

    private UUID projectId;

    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90.")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90.")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180.")
    private Double longitude;

    @Size(max = 10485760, message = "La fotografía excede el tamaño máximo permitido.")
    private String photoUrl;

    private Boolean replace;
}