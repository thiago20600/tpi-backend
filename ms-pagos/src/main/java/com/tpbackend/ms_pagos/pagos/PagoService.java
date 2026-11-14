package com.tpbackend.ms_pagos.pagos;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_pagos.mercado_pago.MercadoPagoClient;
import com.tpbackend.ms_pagos.mercado_pago.dto.MercadoPagoResponseDTO;
import com.tpbackend.ms_pagos.pagos.client.ReservaClient;
import com.tpbackend.ms_pagos.pagos.dto.PagoRequestDTO;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final ReservaClient reservaClient;

    @Transactional
    public Pago crearPago(PagoRequestDTO request) {
        log.info("Creando pago para reserva: {}", request.getReservaId());

        MercadoPagoResponseDTO response = mercadoPagoClient.crearOrden(
                request.getReservaId(), request.getMonto());

        Pago pago = Pago.builder()
                .reservaId(request.getReservaId())
                .monto(request.getMonto())
                .estado("PENDIENTE")
                .qrData(response.getQrData())
                .fechaCreacion(LocalDateTime.now())
                .build();

        Pago guardado = pagoRepository.save(pago);
        log.debug("Pago creado con ID: {}", guardado.getId());
        return guardado;
    }

    @Transactional
    public void procesarWebhook(String paymentId) {
        log.info("Procesando webhook de Mercado Pago: {}", paymentId);

        Pago pago = pagoRepository.findByQrData(paymentId);
        if (pago == null) {
            log.warn("No se encontro pago para el qrData: {}", paymentId);
            throw new BusinessException("Pago no encontrado", 404);
        }

        pago.setEstado("PAGADO");
        pagoRepository.save(pago);

        reservaClient.confirmarReserva(pago.getReservaId());
        log.info("Reserva confirmada: {}", pago.getReservaId());
    }

    @Transactional
    public void cancelarPago(UUID id) {
        log.info("Cancelando pago: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado: {}", id);
                    return new BusinessException("Pago no encontrado", 404);
                });

        pago.setEstado("CANCELADO");
        pagoRepository.save(pago);

        reservaClient.cancelarReserva(pago.getReservaId());
        log.info("Reserva cancelada: {}", pago.getReservaId());
    }
}