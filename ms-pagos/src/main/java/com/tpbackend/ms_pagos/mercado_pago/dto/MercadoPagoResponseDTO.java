package com.tpbackend.ms_pagos.mercado_pago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MercadoPagoResponseDTO {
    private String id;
    private String status;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("checkout_url")
    private String checkoutUrl;

    private Transactions transactions;

    public boolean tienePagoProcesado() {
        return transactions != null
                && transactions.getPayments() != null
                && transactions.getPayments().stream()
                .map(Payment::getStatus)
                .anyMatch(status -> "processed".equalsIgnoreCase(status)
                        || "approved".equalsIgnoreCase(status));
    }

    @Data
    public static class Transactions {
        private List<Payment> payments;
    }

    @Data
    public static class Payment {
        private String status;
    }
}