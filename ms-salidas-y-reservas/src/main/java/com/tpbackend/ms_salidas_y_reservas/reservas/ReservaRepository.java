package com.tpbackend.ms_salidas_y_reservas.reservas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    Page<Reserva> findByTitularSub(String titularSub, Pageable pageable);

    Page<Reserva> findBySalidaIdIn(List<UUID> salidaIds, Pageable pageable);

    List<Reserva> findBySalidaIdAndEstado(UUID salidaId, EstadoReserva estado);

    List<Reserva> findBySalidaIdAndEstadoIn(UUID salidaId, List<EstadoReserva> estados);
}