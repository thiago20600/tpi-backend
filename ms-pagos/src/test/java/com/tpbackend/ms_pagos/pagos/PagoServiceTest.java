package com.tpbackend.ms_pagos.pagos;

import com.tpbackend.ms_pagos.mercado_pago.MercadoPagoClient;
import com.tpbackend.ms_pagos.mercado_pago.MercadoPagoWebhookSignatureValidator;
import com.tpbackend.ms_pagos.mercado_pago.dto.MercadoPagoResponseDTO;
import com.tpbackend.ms_pagos.pagos.client.ReservaClient;
import com.tpbackend.ms_pagos.pagos.client.ReservaClient.ReservaDTO;
import com.tpbackend.ms_pagos.pagos.dto.PagoRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private MercadoPagoClient mercadoPagoClient;
    @Mock
    private MercadoPagoWebhookSignatureValidator signatureValidator;
    @Mock
    private ReservaClient reservaClient;
    @InjectMocks
    private PagoService pagoService;

    @Test
    void createsOrderWithAmountFromReservation() {
        UUID reservaId = UUID.randomUUID();
        AtomicReference<UUID> pagoId = new AtomicReference<>();
        BigDecimal montoReserva = new BigDecimal("125.50");
        ReservaDTO reserva = new ReservaDTO();
        reserva.setTitularSub("user-123");
        reserva.setEstado("PENDIENTE_DE_PAGO");
        reserva.setMonto(montoReserva);

        MercadoPagoResponseDTO orden = new MercadoPagoResponseDTO();
        orden.setId("order-123");
        orden.setCheckoutUrl("https://checkout.mercadopago.com/test");

        when(reservaClient.obtenerReserva(reservaId)).thenReturn(reserva);
        when(pagoRepository.findFirstByReservaIdOrderByFechaCreacionDesc(reservaId))
                .thenReturn(Optional.empty());
        when(mercadoPagoClient.crearOrden(
                org.mockito.ArgumentMatchers.eq(reservaId),
                org.mockito.ArgumentMatchers.eq(montoReserva),
                org.mockito.ArgumentMatchers.eq("user@example.com"),
                org.mockito.ArgumentMatchers.any(UUID.class)))
                .thenReturn(orden);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pagoGuardado = invocation.getArgument(0);
            if (pagoGuardado.getId() == null) {
                pagoGuardado.setId(UUID.randomUUID());
            }
            pagoId.set(pagoGuardado.getId());
            return pagoGuardado;
        });

        PagoRequestDTO request = new PagoRequestDTO();
        request.setReservaId(reservaId);
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user-123")
                .claim("email", "user@example.com")
                .build();

        Pago pago = pagoService.crearPago(request, jwt);

        assertEquals(montoReserva, pago.getMonto());
        assertEquals("https://checkout.mercadopago.com/test", pago.getCheckoutUrl());
        verify(mercadoPagoClient).crearOrden(reservaId, montoReserva, "user@example.com", pagoId.get());
    }
}
