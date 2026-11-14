package com.tpbackend.ms_pagos.pagos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    Pago findByQrData(String qrData);
}