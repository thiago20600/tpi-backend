package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

import java.util.UUID;

public class SalidaNoEncontradaException extends BusinessException {
    public SalidaNoEncontradaException(UUID id) {
        super("Salida no encontrada: " + id, 404);
    }
}