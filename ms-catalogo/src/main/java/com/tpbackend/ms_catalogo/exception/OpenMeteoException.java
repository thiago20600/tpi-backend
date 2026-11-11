package com.tpbackend.ms_catalogo.exception;

import com.tpbackend.BusinessException;

public class OpenMeteoException extends BusinessException{
    
        public OpenMeteoException(String mensaje) {
        super(mensaje, 503);
    }

}
