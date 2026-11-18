package com.tpbackend.ms_pagos.mercado_pago.exceptions;

import com.tpbackend.BusinessException;

public class MercadoPagoFirmaWebhookInvalidaException extends BusinessException {

    public MercadoPagoFirmaWebhookInvalidaException() {
        super("La firma del webhook es inválida", 401);
    }
}
