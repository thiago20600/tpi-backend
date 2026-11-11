package com.tpbackend.ms_catalogo.evaluacion;


import com.tpbackend.ms_catalogo.actividad.Actividad;
import com.tpbackend.ms_catalogo.actividad.ActividadRepository;
import com.tpbackend.ms_catalogo.evaluacion.dto.EvaluacionRequestDTO;
import com.tpbackend.ms_catalogo.evaluacion.dto.EvaluacionResponseDTO;
import com.tpbackend.ms_catalogo.evaluacion.dto.OpenMeteoResponseDTO;
import com.tpbackend.ms_catalogo.exception.ActividadNoEncontradaException;
import com.tpbackend.ms_catalogo.exception.IntervaloNoValidoException;
import com.tpbackend.ms_catalogo.exception.OpenMeteoException;
import com.tpbackend.ms_catalogo.exception.PropiedadNoAutorizadaException;

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
                .orElseThrow(() -> new ActividadNoEncontradaException(actividadId));

        if (!actividad.getPrestadorSub().equals(prestadorSub)) {
            throw new PropiedadNoAutorizadaException("No tenes permiso para evaluar esta actividad");
        }

        if (request.getInicio() == null || request.getFin() == null) {
            throw new IntervaloNoValidoException("El intervalo de la salida es obligatorio");
        }
        if (request.getInicio().isAfter(request.getFin())) {
            throw new IntervaloNoValidoException("La fecha de inicio no puede ser posterior a la de fin");
        }

        OpenMeteoResponseDTO response = openMeteoClient.obtenerRafagas(
                actividad.getLatitud(), actividad.getLongitud(), request.getInicio(), request.getFin());

        if (response == null || response.getHourly() == null) {
            throw new OpenMeteoException("Open-Meteo devolvio una respuesta vacia");
        }

        List<Double> rafagas = response.getHourly().getWindGusts10m();
        if (rafagas == null || rafagas.isEmpty()) {
            throw new OpenMeteoException("Open-Meteo no devolvio datos de rafagas para ese intervalo");
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