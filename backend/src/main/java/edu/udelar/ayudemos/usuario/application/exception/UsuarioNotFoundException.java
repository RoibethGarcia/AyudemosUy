package edu.udelar.ayudemos.usuario.application.exception;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(final Long id) {
        super("No existe un usuario base con id: " + id);
    }
}