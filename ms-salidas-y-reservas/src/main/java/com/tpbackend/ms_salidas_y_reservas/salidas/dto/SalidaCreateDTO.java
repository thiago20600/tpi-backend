package com.tpbackend.ms_salidas_y_reservas.salidas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SalidaCreateDTO {

    @NotNull(message = "El ID de la actividad es obligatorio")
    private UUID actividadId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime inicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fin;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a cero")
    private Integer capacidad;
}