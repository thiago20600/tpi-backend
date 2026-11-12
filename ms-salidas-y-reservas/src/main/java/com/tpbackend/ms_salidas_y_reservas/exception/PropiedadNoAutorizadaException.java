package com.tpbackend.ms_salidas_y_reservas.exception;

import com.tpbackend.BusinessException;

public class PropiedadNoAutorizadaException extends BusinessException{

    public PropiedadNoAutorizadaException() {
        super("No se tienen los permisos necesarios para realizar esta operacion", 403);
    }


}
