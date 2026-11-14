package com.tpbackend.ms_pagos.exceptions;

import com.tpbackend.BusinessException;

public class ReservaNoPendientePagoException extends BusinessException {

    public ReservaNoPendientePagoException() {
        super("La reserva no está pendiente de pago", 409);
    }
}
