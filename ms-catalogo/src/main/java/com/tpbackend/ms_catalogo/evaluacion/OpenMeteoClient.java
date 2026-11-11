package com.tpbackend.ms_catalogo.evaluacion;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_catalogo.evaluacion.dto.OpenMeteoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OpenMeteoClient {

    private final RestClient openMeteoRestClient;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public OpenMeteoResponseDTO obtenerRafagas(Double latitud, Double longitud, LocalDateTime inicio, LocalDateTime fin) {
        try {
            return openMeteoRestClient.get()
                    .uri("/v1/forecast?latitude={lat}&longitude={lon}" +
                         "&hourly=wind_gusts_10m" +
                         "&start_hour={inicio}&end_hour={fin}" +
                         "&timezone=UTC", 
                         latitud, longitud, 
                         inicio.format(FORMATTER), 
                         fin.format(FORMATTER))
                    .retrieve()
                    .body(OpenMeteoResponseDTO.class);
        } catch (RestClientResponseException ex) {
            throw new BusinessException("Error al consultar Open-Meteo: " + ex.getStatusCode(), 503);
        } catch (Exception ex) {
            throw new BusinessException("Error de conexión con Open-Meteo: " + ex.getMessage(), 503);
        }
    }
}