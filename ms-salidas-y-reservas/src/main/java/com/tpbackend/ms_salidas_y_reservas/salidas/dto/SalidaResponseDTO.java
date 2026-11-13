package com.tpbackend.ms_salidas_y_reservas.salidas.dto;

import com.tpbackend.ms_salidas_y_reservas.salidas.EstadoSalida;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SalidaResponseDTO {
    private UUID id;
    private UUID actividadId;
    private String prestadorSub;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private Integer capacidad;
    private EstadoSalida estado;
}