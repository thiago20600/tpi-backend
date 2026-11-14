package com.tpbackend.ms_pagos.exceptions;

import com.tpbackend.BusinessException;

public class ReservaNoPerteneceUsuarioException extends BusinessException {

    public ReservaNoPerteneceUsuarioException() {
        super("La reserva no pertenece al usuario autenticado", 403);
    }
}
