package com.tpbackend.ms_pagos.pagos;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_pagos.mercado_pago.MercadoPagoClient;
import com.tpbackend.ms_pagos.mercado_pago.MercadoPagoWebhookSignatureValidator;
import com.tpbackend.ms_pagos.mercado_pago.dto.MercadoPagoResponseDTO;
import com.tpbackend.ms_pagos.pagos.client.ReservaClient;
import com.tpbackend.ms_pagos.pagos.client.ReservaClient.ReservaDTO;
import com.tpbackend.ms_pagos.pagos.dto.PagoRequestDTO;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final MercadoPagoWebhookSignatureValidator signatureValidator;
    private final ReservaClient reservaClient;

    public Pago crearPago(PagoRequestDTO request, Jwt jwt) {
        log.info("Creando pago para reserva: {}", request.getReservaId());

        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new BusinessException("El usuario no tiene un email configurado", 400);
        }

        ReservaDTO reserva = reservaClient.obtenerReserva(request.getReservaId());
        if (!jwt.getSubject().equals(reserva.getTitularSub())) {
            throw new BusinessException("La reserva no pertenece al usuario autenticado", 403);
        }

        Pago pago = pagoRepository.findFirstByReservaIdOrderByFechaCreacionDesc(request.getReservaId())
                .orElseGet(() -> Pago.builder()
                        .reservaId(request.getReservaId())
                        .fechaCreacion(LocalDateTime.now())
                        .build());

        if (pago.getCheckoutUrl() != null) {
            return pago;
        }
        if (!"PENDIENTE_DE_PAGO".equals(reserva.getEstado())) {
            throw new BusinessException("La reserva no está pendiente de pago", 409);
        }
        if (reserva.getMonto() == null || reserva.getMonto().signum() <= 0) {
            throw new BusinessException("La reserva tiene un monto inválido", 400);
        }

        pago.setMonto(reserva.getMonto());
        pago.setEstado(EstadoPago.PENDIENTE);
        pago = pagoRepository.save(pago);

        MercadoPagoResponseDTO response = mercadoPagoClient.crearOrden(
                request.getReservaId(), reserva.getMonto(), email, pago.getId());
        if (response == null || response.getId() == null || response.getCheckoutUrl() == null) {
            throw new BusinessException("Mercado Pago no devolvió el identificador de orden y checkout_url", 502);
        }
        if (response.getExternalReference() != null
                && !request.getReservaId().toString().equals(response.getExternalReference())) {
            throw new BusinessException("Mercado Pago devolvió una referencia externa inesperada", 502);
        }

        pago.setMercadoPagoOrderId(response.getId());
        pago.setCheckoutUrl(response.getCheckoutUrl());
        Pago guardado = pagoRepository.save(pago);
        log.debug("Pago creado con ID: {}", guardado.getId());
        return guardado;
    }

    public void procesarWebhook(String orderId, String signature, String requestId) {
        log.info("Procesando webhook de Mercado Pago: {}", orderId);
        signatureValidator.validar(orderId, signature, requestId);

        MercadoPagoResponseDTO orden = mercadoPagoClient.obtenerOrden(orderId);
        if (orden == null) {
            throw new BusinessException("Mercado Pago no devolvió la orden", 502);
        }

        Pago pago = pagoRepository.findByMercadoPagoOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("No se encontró pago para la orden: {}", orderId);
                    return new BusinessException("Pago no encontrado", 404);
                });

        if (!orden.tienePagoProcesado() || pago.getEstado() == EstadoPago.PAGADO) {
            return;
        }

        reservaClient.confirmarReserva(pago.getReservaId());
        pago.setEstado(EstadoPago.PAGADO);
        pagoRepository.save(pago);
        log.info("Reserva confirmada: {}", pago.getReservaId());
    }

    public void cancelarPago(UUID id) {
        log.info("Cancelando pago: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado: {}", id);
                    return new BusinessException("Pago no encontrado", 404);
                });

        pago.setEstado(EstadoPago.CANCELADO);
        pagoRepository.save(pago);

        reservaClient.cancelarReserva(pago.getReservaId());
        log.info("Reserva cancelada: {}", pago.getReservaId());
    }
}