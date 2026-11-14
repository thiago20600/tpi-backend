package com.tpbackend.ms_pagos.mercado_pago;

import com.tpbackend.BusinessException;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MercadoPagoWebhookSignatureValidatorTest {

    private static final String SECRET = "webhook-secret";
    private static final String ORDER_ID = "order-123";
    private static final String REQUEST_ID = "request-456";
    private static final String TIMESTAMP = "1730000000";

    @Test
    void acceptsValidSignature() throws Exception {
        String signature = signatureFor(ORDER_ID);

        assertDoesNotThrow(() ->
                new MercadoPagoWebhookSignatureValidator(SECRET)
                        .validar(ORDER_ID, signature, REQUEST_ID));
    }

    @Test
    void rejectsSignatureForDifferentOrder() throws Exception {
        String signature = signatureFor(ORDER_ID);

        assertThrows(BusinessException.class, () ->
                new MercadoPagoWebhookSignatureValidator(SECRET)
                        .validar("different-order", signature, REQUEST_ID));
    }

    private String signatureFor(String orderId) throws Exception {
        String manifest = "id:" + orderId.toLowerCase()
                + ";request-id:" + REQUEST_ID
                + ";ts:" + TIMESTAMP + ";";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String hash = HexFormat.of().formatHex(mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8)));
        return "ts=" + TIMESTAMP + ",v1=" + hash;
    }
}
