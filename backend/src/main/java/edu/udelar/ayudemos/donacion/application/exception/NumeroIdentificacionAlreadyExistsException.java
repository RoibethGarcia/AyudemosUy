package edu.udelar.ayudemos.donacion.application.exception;

public class NumeroIdentificacionAlreadyExistsException extends RuntimeException {

    public NumeroIdentificacionAlreadyExistsException(final String numeroIdentificacion) {
        super("Ya existe una donacion con numero de identificacion " + numeroIdentificacion);
    }
}
