package com.tpbackend.ms_catalogo.actividad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity 
@Table(name = "actividades")
@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder 
@EqualsAndHashCode(of = "id")
public class Actividad {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(nullable = false)
    private Double limiteRafagasKmh;

    @Column(nullable = false, length = 100)
    private String prestadorSub;

}
