package com.tpbackend.ms_salidas_y_reservas.salidas.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluacionRequestDTO {
    private LocalDateTime inicio;
    private LocalDateTime fin;
}