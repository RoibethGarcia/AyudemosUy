package edu.udelar.ayudemos.common.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String correo) {
        super("El correo electronico ya esta registrado: " + correo);
    }
}

