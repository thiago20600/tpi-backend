package com.tpbackend.ms_pagos.pagos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID de la reserva es obligatorio")
    private UUID reservaId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;
}