package com.tpbackend.ms_pagos.exceptions;

import com.tpbackend.BusinessException;

public class MontoReservaInvalidoException extends BusinessException {

    public MontoReservaInvalidoException() {
        super("La reserva tiene un monto inválido", 400);
    }
}
