package com.tpbackend.ms_salidas_y_reservas.reservas;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository 
public interface ReservaRepository extends JpaRepository<Reserva, UUID>{

    Page<Reserva> findByTitularSub(String titularSub, Pageable pageable);
    Page<Reserva> findBySalidaIdIn(List<UUID> salidaIds, Pageable pageable);
}
