package com.tpbackend.ms_pagos.pagos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PagoResponseDTO {
    private UUID id;
    private UUID reservaId;
    private BigDecimal monto;
    private String estado;
    private String qrData;
    private LocalDateTime fechaCreacion;
}