package com.springboot.riwitask.exception;

public class DupicateEmailException extends RuntimeException{
    public DupicateEmailException(String mensaje){
        super(mensaje);
    }
}
