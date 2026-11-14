package com.tpbackend.ms_pagos.pagos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    Optional<Pago> findByMercadoPagoOrderId(String mercadoPagoOrderId);
    Optional<Pago> findFirstByReservaIdOrderByFechaCreacionDesc(UUID reservaId);
}