package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class CapacidadExcedidaException extends BusinessException {
    public CapacidadExcedidaException(String mensaje) {
        super(mensaje, 409);
    }
}