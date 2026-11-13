package com.tpbackend.ms_salidas_y_reservas.reservas.dto;

import com.tpbackend.ms_salidas_y_reservas.reservas.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservaResponseDTO {
    private UUID id;
    private UUID salidaId;
    private String titularSub;
    private Integer cantidad;
    private LocalDateTime fechaCreacion;
    private EstadoReserva estado;
    private Boolean afectada;
    private BigDecimal monto;
}