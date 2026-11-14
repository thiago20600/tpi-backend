package com.tpbackend.ms_pagos.mercado_pago;

import com.tpbackend.BusinessException;
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

    private final String webhookSecret;

    public MercadoPagoWebhookSignatureValidator(@Value("${mercadopago.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public void validar(String orderId, String signature, String requestId) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new BusinessException("El secreto del webhook no está configurado", 503);
        }
        if (signature == null || requestId == null || requestId.isBlank()) {
            throw new BusinessException("Faltan encabezados de firma del webhook", 401);
        }

        Map<String, String> signatureValues = parseSignature(signature);
        String timestamp = signatureValues.get("ts");
        String receivedHash = signatureValues.get("v1");
        if (timestamp == null || receivedHash == null) {
            throw new BusinessException("La firma del webhook es inválida", 401);
        }

        String manifest = "id:" + orderId.toLowerCase(Locale.ROOT)
                + ";request-id:" + requestId
                + ";ts:" + timestamp + ";";
        String expectedHash = calculateHash(manifest);
        if (!MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.US_ASCII),
                receivedHash.getBytes(StandardCharsets.US_ASCII))) {
            throw new BusinessException("La firma del webhook es inválida", 401);
        }
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
            throw new IllegalStateException("No se pudo calcular la firma HMAC del webhook", exception);
        }
    }
}
