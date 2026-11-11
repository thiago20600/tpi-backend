package com.tpbackend.ms_catalogo.evaluacion;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_catalogo.actividad.Actividad;
import com.tpbackend.ms_catalogo.actividad.ActividadRepository;
import com.tpbackend.ms_catalogo.evaluacion.dto.EvaluacionResponseDTO;
import com.tpbackend.ms_catalogo.evaluacion.dto.OpenMeteoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final ActividadRepository actividadRepository;
    private final OpenMeteoClient openMeteoClient;

    public EvaluacionResponseDTO evaluar(UUID actividadId, String prestadorSub) {

        Actividad actividad = actividadRepository.findById(actividadId).orElseThrow(() -> new BusinessException("Actividad no encontrada: " + actividadId, 404));


        if (!actividad.getPrestadorSub().equals(prestadorSub)) {
            throw new BusinessException("No tenes permiso para evaluar esta actividad", 403);
        }

        OpenMeteoResponseDTO response = openMeteoClient.obtenerRafagas(actividad.getLatitud(), actividad.getLongitud());

        Double maximaRafaga = response.getHourly().getWindGusts10m().stream()
                .max(Double::compare)
                .orElse(0.0);
                
        ResultadoEvaluacion resultado = maximaRafaga > actividad.getLimiteRafagasKmh()
                ? ResultadoEvaluacion.SUPERA_UMBRAL
                : ResultadoEvaluacion.NO_SUPERA_UMBRAL;

        EvaluacionResponseDTO dto = new EvaluacionResponseDTO();
        dto.setResultado(resultado);
        dto.setMaximaRafagaKmh(maximaRafaga);
        dto.setLimiteRafagasKmh(actividad.getLimiteRafagasKmh());
        return dto;
    }
}