package edu.udelar.ayudemos.repartidor.application.exception;

public class NumeroLicenciaAlreadyExistsException extends RuntimeException {

    public NumeroLicenciaAlreadyExistsException(final String numeroLicencia) {
        super("El numero de licencia ya esta registrado: " + numeroLicencia);
    }
}
