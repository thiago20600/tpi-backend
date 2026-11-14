package com.tpbackend.ms_pagos.pagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID de la reserva es obligatorio")
    private UUID reservaId;
}