package com.tpbackend.ms_catalogo.evaluacion.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EvaluacionRequestDTO {
    private LocalDateTime inicio;
    private LocalDateTime fin;
}