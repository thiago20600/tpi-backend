package com.tpbackend.ms_salidas_y_reservas.reservas.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tpbackend.ms_salidas_y_reservas.reservas.EstadoReserva;

@Data
public class ReservaResponseDTO {
    private UUID id;
    private UUID salidaId;
    private String titularSub;
    private Integer cantidad;
    private LocalDateTime fechaCreacion;
    private EstadoReserva estado;
    private Boolean afectada;
}
