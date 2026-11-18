package com.tpbackend.ms_pagos.mercado_pago;

import com.tpbackend.ms_pagos.mercado_pago.exceptions.MercadoPagoFirmaWebhookInvalidaException;
import com.tpbackend.ms_pagos.mercado_pago.exceptions.MercadoPagoWebhookHeadersFaltantesException;
import com.tpbackend.ms_pagos.mercado_pago.exceptions.MercadoPagoWebhookSecretNoConfiguradoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class MercadoPagoWebhookSignatureValidator {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoWebhookSignatureValidator.class);

    private final String webhookSecret;

    public MercadoPagoWebhookSignatureValidator(@Value("${mercadopago.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public void validar(String orderId, String signature, String requestId) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.error("No está configurado el secreto del webhook de Mercado Pago");
            throw new MercadoPagoWebhookSecretNoConfiguradoException();
        }
        if (signature == null || requestId == null || requestId.isBlank()) {
            log.warn("Webhook de Mercado Pago rechazado: faltan encabezados de firma");
            throw new MercadoPagoWebhookHeadersFaltantesException();
        }

        Map<String, String> signatureValues = parseSignature(signature);
        String timestamp = signatureValues.get("ts");
        String receivedHash = signatureValues.get("v1");
        if (timestamp == null || receivedHash == null) {
            log.warn("Webhook de Mercado Pago rechazado: formato de firma inválido");
            throw new MercadoPagoFirmaWebhookInvalidaException();
        }

        String manifest = "id:" + orderId.toLowerCase(Locale.ROOT)
                + ";request-id:" + requestId
                + ";ts:" + timestamp + ";";
        String expectedHash = calculateHash(manifest);
        if (!MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.US_ASCII),
                receivedHash.getBytes(StandardCharsets.US_ASCII))) {
            log.warn("Webhook de Mercado Pago rechazado: firma no coincide para la orden {}", orderId);
            throw new MercadoPagoFirmaWebhookInvalidaException();
        }
        log.debug("Firma del webhook de Mercado Pago validada para la orden {}", orderId);
    }

    private Map<String, String> parseSignature(String signature) {
        Map<String, String> values = new HashMap<>();
        for (String part : signature.split(",")) {
            String[] entry = part.trim().split("=", 2);
            if (entry.length == 2) {
                values.put(entry[0], entry[1]);
            }
        }
        return values;
    }

    private String calculateHash(String manifest) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (GeneralSecurityException exception) {
            log.error("No se pudo calcular la firma HMAC del webhook", exception);
            throw new IllegalStateException("No se pudo calcular la firma HMAC del webhook", exception);
        }
    }
}
