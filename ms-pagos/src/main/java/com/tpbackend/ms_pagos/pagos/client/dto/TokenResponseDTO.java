package com.tpbackend.ms_pagos.pagos.client.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

}
