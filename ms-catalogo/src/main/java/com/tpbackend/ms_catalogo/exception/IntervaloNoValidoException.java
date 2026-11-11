package com.tpbackend.ms_catalogo.exception;

import com.tpbackend.BusinessException;

public class IntervaloNoValidoException extends BusinessException {
    public IntervaloNoValidoException(String mensaje) {
        super(mensaje, 400);
    }
}