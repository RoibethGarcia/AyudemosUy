package edu.udelar.ayudemos.auth.application.exception;

public class UnauthenticatedSessionException extends RuntimeException {

    public UnauthenticatedSessionException() {
        super("No hay una sesión autenticada activa");
    }
}
