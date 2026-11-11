package com.tpbackend.ms_catalogo.evaluacion.dto;

import com.tpbackend.ms_catalogo.evaluacion.ResultadoEvaluacion;

import lombok.Data;

@Data 
public class EvaluacionResponseDTO {

    private ResultadoEvaluacion resultado;         
    private Double maximaRafagaKmh;     
    private Double limiteRafagasKmh; 


}
