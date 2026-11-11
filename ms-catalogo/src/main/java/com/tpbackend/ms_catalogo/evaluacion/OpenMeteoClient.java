package com.tpbackend.ms_catalogo.evaluacion;

import com.tpbackend.ms_catalogo.evaluacion.dto.OpenMeteoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OpenMeteoClient {
    
    private final RestClient openMeteoRestClient;

    public OpenMeteoResponseDTO obtenerRafagas(Double latitud, Double longitud) {
        return openMeteoRestClient.get()
                .uri("/forecast?latitude={lat}&longitude={lon}&hourly=wind_gusts_10m&timezone=UTC", latitud, longitud)
                .retrieve()
                .body(OpenMeteoResponseDTO.class);
    }

}
