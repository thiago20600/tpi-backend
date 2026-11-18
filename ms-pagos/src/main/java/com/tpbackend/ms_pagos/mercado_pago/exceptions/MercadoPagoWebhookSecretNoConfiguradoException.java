package com.tpbackend.ms_pagos.mercado_pago.exceptions;

import com.tpbackend.BusinessException;

public class MercadoPagoWebhookSecretNoConfiguradoException extends BusinessException {

    public MercadoPagoWebhookSecretNoConfiguradoException() {
        super("El secreto del webhook no está configurado", 503);
    }
}
