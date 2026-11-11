package com.tpbackend.ms_catalogo.evaluacion;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_catalogo.actividad.Actividad;
import com.tpbackend.ms_catalogo.actividad.ActividadRepository;
import com.tpbackend.ms_catalogo.evaluacion.dto.EvaluacionRequestDTO;
import com.tpbackend.ms_catalogo.evaluacion.dto.EvaluacionResponseDTO;
import com.tpbackend.ms_catalogo.evaluacion.dto.OpenMeteoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final ActividadRepository actividadRepository;
    private final OpenMeteoClient openMeteoClient;

    public EvaluacionResponseDTO evaluar(UUID actividadId, EvaluacionRequestDTO request, String prestadorSub) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new BusinessException("Actividad no encontrada: " + actividadId, 404));

        if (!actividad.getPrestadorSub().equals(prestadorSub)) {
            throw new BusinessException("No tenes permiso para evaluar esta actividad", 403);
        }

        if (request.getInicio() == null || request.getFin() == null) {
            throw new BusinessException("El intervalo de la salida es obligatorio", 400);
        }
        if (request.getInicio().isAfter(request.getFin())) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la de fin", 400);
        }

        OpenMeteoResponseDTO response = openMeteoClient.obtenerRafagas(
                actividad.getLatitud(), actividad.getLongitud(), request.getInicio(), request.getFin());

        if (response == null || response.getHourly() == null) {
            throw new BusinessException("Open-Meteo devolvio una respuesta vacia", 503);
        }

        List<Double> rafagas = response.getHourly().getWindGusts10m();
        if (rafagas == null || rafagas.isEmpty()) {
            throw new BusinessException("Open-Meteo no devolvio datos de rafagas para ese intervalo", 503);
        }

        Double maximaRafaga = rafagas.stream().max(Double::compare).orElse(0.0);

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