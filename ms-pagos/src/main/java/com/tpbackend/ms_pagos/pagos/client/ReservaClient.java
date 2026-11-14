package com.tpbackend.ms_pagos.pagos.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservaClient {

    private final RestClient reservaRestClient;
    private final TokenService tokenService;

    public ReservaDTO obtenerReserva(UUID reservaId) {
        String token = tokenService.obtenerToken();
        return reservaRestClient.get()
                .uri("/api/v1/reservas/{id}", reservaId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ReservaDTO.class);
    }

    public void confirmarReserva(UUID reservaId) {
        String token = tokenService.obtenerToken();
        reservaRestClient.post()
                .uri("/api/v1/reservas/{id}/confirmacion", reservaId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    public void cancelarReserva(UUID reservaId) {
        String token = tokenService.obtenerToken();
        reservaRestClient.post()
                .uri("/api/v1/reservas/{id}/cancelacion", reservaId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    @lombok.Data
    public static class ReservaDTO {
        private UUID id;
        private String titularSub;
        private String estado;
        private BigDecimal monto;
    }
}