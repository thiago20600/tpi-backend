package com.tpbackend.ms_salidas_y_reservas.salidas;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalidaRepository extends JpaRepository<Salida, UUID>{

    List<Salida> findByPrestadorSub(String prestadorSub);
}
