package edu.udelar.ayudemos.usuario.api.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo
) {
}