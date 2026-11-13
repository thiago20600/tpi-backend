package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class DiaOcupadoException extends BusinessException {

    public DiaOcupadoException() {
        super("Ya tenes una reserva para ese dia", 409);
        //TODO Auto-generated constructor stub
    }

}
