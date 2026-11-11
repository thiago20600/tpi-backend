package com.tpbackend.ms_catalogo.exception;

import com.tpbackend.BusinessException;
import java.util.UUID;

public class ActividadNoEncontradaException extends BusinessException {
    public ActividadNoEncontradaException(UUID id) {
        super("Actividad no encontrada: " + id, 404);
    }
}