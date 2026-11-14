package com.tpbackend.ms_pagos.pagos.dto;

import com.tpbackend.ms_pagos.pagos.EstadoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PagoResponseDTO {
    private UUID id;
    private UUID reservaId;
    private BigDecimal monto;
    private EstadoPago estado;
    private String mercadoPagoOrderId;
    private String checkoutUrl;
    private LocalDateTime fechaCreacion;
}