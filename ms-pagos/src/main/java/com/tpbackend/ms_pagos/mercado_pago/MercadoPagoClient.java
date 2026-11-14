package com.tpbackend.ms_pagos.mercado_pago;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.tpbackend.ms_pagos.mercado_pago.dto.MercadoPagoResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MercadoPagoClient {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoClient.class);

    private final RestClient mercadoPagoRestClient;

    public MercadoPagoResponseDTO crearOrden(UUID reservaId, BigDecimal monto) {
        log.info("Llamando a Mercado Pago para crear orden - reserva: {} - monto: {}", reservaId, monto);

        Map<String, Object> body = Map.of(
                "type", "qr",
                "total_amount", monto,
                "description", "Reserva " + reservaId,
                "items", List.of(Map.of(
                        "title", "Reserva",
                        "quantity", 1,
                        "unit_price", monto
                ))
        );

        return mercadoPagoRestClient.post()
                .uri("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(MercadoPagoResponseDTO.class);
    }
}