package edu.udelar.ayudemos.auth.application.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Correo o contraseña inválidos");
    }
}
