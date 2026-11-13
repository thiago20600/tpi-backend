package com.tpbackend.ms_salidas_y_reservas.salidas.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ActividadDTO {
    private UUID id;
    private String nombre;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private Double limiteRafagasKmh;
    private String prestadorSub;
}