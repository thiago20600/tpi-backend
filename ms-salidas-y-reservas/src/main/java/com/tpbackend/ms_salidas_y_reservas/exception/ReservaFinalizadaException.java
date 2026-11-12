package com.tpbackend.ms_salidas_y_reservas.exception;


import com.tpbackend.BusinessException;

public class ReservaFinalizadaException extends BusinessException{

    public ReservaFinalizadaException() {
        super("La reserva no esta pendiente de pago", 400);
    }

}
