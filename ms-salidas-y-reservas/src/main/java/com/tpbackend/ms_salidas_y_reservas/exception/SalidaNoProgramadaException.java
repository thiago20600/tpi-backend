package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class SalidaNoProgramadaException extends BusinessException {
    public SalidaNoProgramadaException() {
        super("La salida no esta programada", 409);
    }
}