package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class SalidaNoIniciadaException extends BusinessException {
    public SalidaNoIniciadaException() {
        super("La salida no esta iniciada", 409);
    }
}