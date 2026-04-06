package edu.udelar.ayudemos.auth.api.dto;

public record AuthResponse(
        Long id,
        String nombre,
        String correo,
        String tipoUsuario
) {
}
