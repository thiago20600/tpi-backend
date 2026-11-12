package com.tpbackend.ms_catalogo.evaluacion;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_catalogo.evaluacion.dto.OpenMeteoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OpenMeteoClient {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoClient.class);

    private final RestClient openMeteoRestClient;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public OpenMeteoResponseDTO obtenerRafagas(Double latitud, Double longitud, LocalDateTime inicio, LocalDateTime fin) {
        String url = String.format(
            "/v1/forecast?latitude=%s&longitude=%s&hourly=wind_gusts_10m&start_hour=%s&end_hour=%s&timezone=UTC",
            latitud, longitud,
            inicio.format(FORMATTER),
            fin.format(FORMATTER)
        );

        log.info("Llamando a Open-Meteo: {}", url);

        try {
            return openMeteoRestClient.get()
                    .uri(url)
                    .retrieve()
                    .body(OpenMeteoResponseDTO.class);
        } catch (RestClientResponseException ex) {
            log.error("Error al consultar Open-Meteo: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new BusinessException("Error al consultar Open-Meteo: " + ex.getStatusCode(), 503);
        } catch (Exception ex) {
            log.error("Error de conexión con Open-Meteo: {}", ex.getMessage(), ex);
            throw new BusinessException("Error de conexión con Open-Meteo: " + ex.getMessage(), 503);
        }
    }
}