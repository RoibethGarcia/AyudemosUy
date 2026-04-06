package edu.udelar.ayudemos.repartidor.api.dto;

public record RepartidorResponse(
        Long id,
        String nombre,
        String correo,
        String numeroLicencia
) {
}
