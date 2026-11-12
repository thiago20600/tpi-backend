package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

import java.util.UUID;

public class ReservaNoEncontradaException extends BusinessException {

    public ReservaNoEncontradaException(UUID id) {
        super("Reserva no encontrada: " + id, 404);
    }
}