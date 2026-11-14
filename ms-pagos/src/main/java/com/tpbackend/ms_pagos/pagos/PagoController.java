package com.tpbackend.ms_pagos.pagos;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpbackend.ms_pagos.pagos.dto.PagoRequestDTO;
import com.tpbackend.ms_pagos.pagos.dto.PagoResponseDTO;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(@Valid @RequestBody PagoRequestDTO request) {
        Pago pago = pagoService.crearPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(pago));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        String paymentId = payload.get("data").toString();
        pagoService.procesarWebhook(paymentId);
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
        response.setQrData(pago.getQrData());
        response.setFechaCreacion(pago.getFechaCreacion());
        return response;
    }
}