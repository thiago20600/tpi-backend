package com.tpbackend.ms_pagos.pagos;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.tpbackend.ms_pagos.pagos.dto.PagoRequestDTO;
import com.tpbackend.ms_pagos.pagos.dto.PagoResponseDTO;

import java.util.Map;
import java.util.UUID;
import com.tpbackend.BusinessException;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(
            @Valid @RequestBody PagoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        Pago pago = pagoService.crearPago(request, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(pago));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestHeader(value = "x-request-id", required = false) String requestId) {
        if (!"order".equalsIgnoreCase(String.valueOf(payload.get("type")))) {
            return ResponseEntity.ok().build();
        }
        Object data = payload.get("data");
        if (!(data instanceof Map<?, ?> dataMap) || dataMap.get("id") == null) {
            throw new BusinessException("La notificación no contiene el ID de la orden", 400);
        }
        pagoService.procesarWebhook(dataMap.get("id").toString(), signature, requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancelacion")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        pagoService.cancelarPago(id);
        return ResponseEntity.ok().build();
    }

    private PagoResponseDTO mapToResponse(Pago pago) {
        PagoResponseDTO response = new PagoResponseDTO();
        response.setId(pago.getId());
        response.setReservaId(pago.getReservaId());
        response.setMonto(pago.getMonto());
        response.setEstado(pago.getEstado());
        response.setMercadoPagoOrderId(pago.getMercadoPagoOrderId());
        response.setCheckoutUrl(pago.getCheckoutUrl());
        response.setFechaCreacion(pago.getFechaCreacion());
        return response;
    }
}