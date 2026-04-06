package edu.udelar.ayudemos.repartidor.application.exception;

public class RepartidorNotFoundException extends RuntimeException {

    public RepartidorNotFoundException(final Long id) {
        super("No existe el repartidor con id " + id);
    }
}
