package com.tpbackend.ms_pagos.mercado_pago.exceptions;

import com.tpbackend.BusinessException;

public class NotificacionMercadoPagoSinOrdenException extends BusinessException {

    public NotificacionMercadoPagoSinOrdenException() {
        super("La notificación no contiene el ID de la orden", 400);
    }
}
