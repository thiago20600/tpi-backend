package com.tpbackend.ms_catalogo.actividad;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, UUID> {
    

}