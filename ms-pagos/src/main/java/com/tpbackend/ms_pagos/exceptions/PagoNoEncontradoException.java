package com.tpbackend.ms_pagos.exceptions;

import com.tpbackend.BusinessException;

public class PagoNoEncontradoException extends BusinessException {

    public PagoNoEncontradoException() {
        super("Pago no encontrado", 404);
    }
}
