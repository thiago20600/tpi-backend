package com.tpbackend.ms_pagos.pagos.client;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.tpbackend.ms_pagos.pagos.client.dto.TokenResponseDTO;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RestClient keycloakRestClient;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String obtenerToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        TokenResponseDTO response = keycloakRestClient.post()
                .uri("/realms/tpi/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(TokenResponseDTO.class);

        return response.getAccessToken();
    }
}