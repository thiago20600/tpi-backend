package com.tpbackend.ms_catalogo.exception;

import com.tpbackend.BusinessException;

public class PropiedadNoAutorizadaException extends BusinessException {
    public PropiedadNoAutorizadaException(String mensaje) {
        super(mensaje, 403);
    }
}