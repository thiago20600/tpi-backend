package com.tpbackend.ms_salidas_y_reservas.reservas;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository 
public interface ReservaRepository extends JpaRepository<Reserva, UUID>{

    List<Reserva> findByTitularSub(String titularSub);
    List<Reserva> findBySalidaIdIn(List<UUID> salidaIds);

}
