package com.tpbackend.ms_salidas_y_reservas.salidas;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalidaRepository extends JpaRepository<Salida, UUID> {

    List<Salida> findByPrestadorSub(String prestadorSub);
    Page<Salida> findByPrestadorSub(String prestadorSub, Pageable pageable);

    List<Salida> findByEstado(EstadoSalida estado);
    Page<Salida> findByEstado(EstadoSalida estado, Pageable pageable);

    Page<Salida> findByPrestadorSubAndActividadId(String prestadorSub, UUID actividadId, Pageable pageable);
    Page<Salida> findByEstadoAndActividadId(EstadoSalida estado, UUID actividadId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Salida s WHERE s.id = :id")
    Optional<Salida> findByIdWithLock(@Param("id") UUID id);
}