package com.tpbackend.ms_catalogo.evaluacion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenMeteoResponseDTO {

    @JsonProperty("hourly")
    private Hourly hourly;

    @Data
    public static class Hourly {

        @JsonProperty("time")
        private List<String> time;

        @JsonProperty("wind_gusts_10m")
        private List<Double> windGusts10m;
    }
}