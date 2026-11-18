package com.tpbackend.ms_pagos.mercado_pago.exceptions;

import com.tpbackend.BusinessException;

public class MercadoPagoException extends BusinessException {

    public MercadoPagoException(String message) {
        super(message, 503);
    }
}
