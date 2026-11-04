package com.tpbackend.ms_catalogo.actividad.dto;


import lombok.Data;
import java.util.UUID;


@Data 
public class ActividadResponseDTO {
    private UUID id;
    private String nombre;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private Double limiteRafagasKmh;
    private String prestadorSub;
}
