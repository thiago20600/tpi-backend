package com.tpbackend.ms_salidas_y_reservas.reservas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class ReservaCreateDTO {

    @NotNull(message = "El ID de la salida es obligatorio")
    private UUID salidaId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;
}