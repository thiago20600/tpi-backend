package com.tpbackend.ms_pagos.mercado_pago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MercadoPagoResponseDTO {
    private String id;
    private String status;

    @JsonProperty("qr_data")
    private String qrData;
}