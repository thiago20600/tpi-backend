package com.tpbackend.ms_catalogo.exception;

import com.tpbackend.BusinessException;

public class LimiteRafagasInvalidoException extends BusinessException {
    public LimiteRafagasInvalidoException() {
        super("El limite de rafagas debe ser mayor a 0", 400);
    }
}