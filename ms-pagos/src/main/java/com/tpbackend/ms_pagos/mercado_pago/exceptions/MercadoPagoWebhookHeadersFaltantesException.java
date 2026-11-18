package com.tpbackend.ms_pagos.mercado_pago.exceptions;

import com.tpbackend.BusinessException;

public class MercadoPagoWebhookHeadersFaltantesException extends BusinessException {

    public MercadoPagoWebhookHeadersFaltantesException() {
        super("Faltan encabezados de firma del webhook", 401);
    }
}
