package com.tpbackend.ms_salidas_y_reservas.salidas.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.tpbackend.ms_salidas_y_reservas.salidas.client.dto.ActividadDTO;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActividadClient {

    private final RestClient catalogoRestClient;

    public ActividadDTO obtenerActividad(UUID actividadId, String token) {
        return catalogoRestClient.get()
                .uri("/api/v1/actividades/{id}", actividadId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ActividadDTO.class);
    }
}