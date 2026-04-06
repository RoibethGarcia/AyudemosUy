package edu.udelar.ayudemos.distribucion.application.exception;

public class DistribucionNotFoundException extends RuntimeException {

    public DistribucionNotFoundException(final Long id) {
        super("No existe la distribucion con id " + id);
    }
}
