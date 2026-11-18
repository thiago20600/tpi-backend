package com.tpbackend.ms_pagos.pagos;

import com.tpbackend.ms_pagos.exceptions.EmailUsuarioNoConfiguradoException;
import com.tpbackend.ms_pagos.exceptions.MontoReservaInvalidoException;
import com.tpbackend.ms_pagos.exceptions.PagoNoEncontradoException;
import com.tpbackend.ms_pagos.exceptions.ReservaNoPendientePagoException;
import com.tpbackend.ms_pagos.exceptions.ReservaNoPerteneceUsuarioException;
import com.tpbackend.ms_pagos.exceptions.RespuestaMercadoPagoException;
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
            log.warn("El usuario no tiene un email configurado para crear el pago de la reserva {}",
                    request.getReservaId());
            throw new EmailUsuarioNoConfiguradoException();
        }

        ReservaDTO reserva = reservaClient.obtenerReserva(request.getReservaId());
        if (!jwt.getSubject().equals(reserva.getTitularSub())) {
            log.warn("El usuario no es titular de la reserva {}", request.getReservaId());
            throw new ReservaNoPerteneceUsuarioException();
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
            log.warn("La reserva {} no está pendiente de pago", request.getReservaId());
            throw new ReservaNoPendientePagoException();
        }
        if (reserva.getMonto() == null || reserva.getMonto().signum() <= 0) {
            log.warn("La reserva {} tiene un monto inválido", request.getReservaId());
            throw new MontoReservaInvalidoException();
        }

        pago.setMonto(reserva.getMonto());
        pago.setEstado(EstadoPago.PENDIENTE);
        pago = pagoRepository.save(pago);

        MercadoPagoResponseDTO response = mercadoPagoClient.crearOrden(
                request.getReservaId(), reserva.getMonto(), email, pago.getId());
        if (response == null || response.getId() == null || response.getCheckoutUrl() == null) {
            log.error("Mercado Pago no devolvió el identificador de orden y checkout_url para la reserva {}",
                    request.getReservaId());
            throw new RespuestaMercadoPagoException(
                    "Mercado Pago no devolvió el identificador de orden y checkout_url");
        }
        if (response.getExternalReference() != null
                && !request.getReservaId().toString().equals(response.getExternalReference())) {
            log.error("Mercado Pago devolvió una referencia externa inesperada para la reserva {}",
                    request.getReservaId());
            throw new RespuestaMercadoPagoException(
                    "Mercado Pago devolvió una referencia externa inesperada");
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
            log.warn("Mercado Pago no devolvió la orden {}", orderId);
            throw new RespuestaMercadoPagoException("Mercado Pago no devolvió la orden");
        }

        Pago pago = pagoRepository.findByMercadoPagoOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("No se encontró pago para la orden: {}", orderId);
                    return new PagoNoEncontradoException();
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
                    return new PagoNoEncontradoException();
                });

        pago.setEstado(EstadoPago.CANCELADO);
        pagoRepository.save(pago);

        reservaClient.cancelarReserva(pago.getReservaId());
        log.info("Reserva cancelada: {}", pago.getReservaId());
    }
}