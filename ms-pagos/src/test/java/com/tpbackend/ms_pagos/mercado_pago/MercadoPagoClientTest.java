package com.tpbackend.ms_pagos.mercado_pago;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class MercadoPagoClientTest {

    @Test
    void sendsOrderAmountsAsStrings() {
        UUID reservaId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.mercadopago.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://api.mercadopago.test/v1/orders"))
                .andExpect(method(POST))
                .andExpect(header("X-Idempotency-Key", idempotencyKey.toString()))
                .andExpect(content().json("""
                        {
                          "total_amount": "125.50",
                          "items": [{"unit_price": "125.50"}]
                        }
                        """, false))
                .andRespond(withSuccess(
                        "{\"id\":\"order-123\",\"checkout_url\":\"https://checkout.test\"}",
                        MediaType.APPLICATION_JSON));

        new MercadoPagoClient(builder.build())
                .crearOrden(reservaId, new BigDecimal("125.50"), "buyer@example.com", idempotencyKey);

        server.verify();
    }
}
