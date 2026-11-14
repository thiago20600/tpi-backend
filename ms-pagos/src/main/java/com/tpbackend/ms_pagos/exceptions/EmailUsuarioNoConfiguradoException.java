package com.tpbackend.ms_pagos.exceptions;

import com.tpbackend.BusinessException;

public class EmailUsuarioNoConfiguradoException extends BusinessException {

    public EmailUsuarioNoConfiguradoException() {
        super("El usuario no tiene un email configurado", 400);
    }
}
