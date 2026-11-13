package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class ReservaNoConfirmadaException extends BusinessException {
    public ReservaNoConfirmadaException() {
        super("La reserva no esta confirmada", 400);
    }
}