package com.tpbackend.ms_salidas_y_reservas.salidas.client.dto;

import lombok.Data;

@Data
public class EvaluacionResponseDTO {
    private String resultado;
    private Double maximaRafagaKmh;
    private Double limiteRafagasKmh;
}