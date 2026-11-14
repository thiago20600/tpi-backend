package com.tpbackend.ms_pagos.exceptions;

import com.tpbackend.BusinessException;

public class RespuestaMercadoPagoException extends BusinessException {

    public RespuestaMercadoPagoException(String message) {
        super(message, 502);
    }
}
