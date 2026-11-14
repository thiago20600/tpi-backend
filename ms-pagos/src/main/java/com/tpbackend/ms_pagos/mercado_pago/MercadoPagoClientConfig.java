package com.tpbackend.ms_pagos.mercado_pago;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MercadoPagoClientConfig {

    @Bean
    public RestClient mercadoPagoRestClient(
            @Value("${mercadopago.base-url}") String baseUrl,
            @Value("${mercadopago.access-token}") String accessToken) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }
}