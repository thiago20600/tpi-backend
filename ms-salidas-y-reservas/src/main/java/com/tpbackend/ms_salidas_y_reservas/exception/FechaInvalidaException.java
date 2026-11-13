package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class FechaInvalidaException extends BusinessException {
    public FechaInvalidaException(String mensaje) {
        super(mensaje, 400);
    }
}