package com.tpbackend.ms_salidas_y_reservas.salidas.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.tpbackend.ms_salidas_y_reservas.salidas.client.dto.EvaluacionRequestDTO;
import com.tpbackend.ms_salidas_y_reservas.salidas.client.dto.EvaluacionResponseDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionClient {

    private final RestClient catalogoRestClient;

    public EvaluacionResponseDTO evaluarViento(UUID actividadId, LocalDateTime inicio, LocalDateTime fin, String token) {
        EvaluacionRequestDTO request = new EvaluacionRequestDTO();
        request.setInicio(inicio);
        request.setFin(fin);

        return catalogoRestClient.post()
                .uri("/api/v1/actividades/{id}/evaluaciones-viento", actividadId)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(EvaluacionResponseDTO.class);
    }
}