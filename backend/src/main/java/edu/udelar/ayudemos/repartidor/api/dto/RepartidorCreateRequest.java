package edu.udelar.ayudemos.repartidor.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RepartidorCreateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        String correo,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,
        @NotBlank(message = "El numero de licencia es obligatorio")
        String numeroLicencia
) {
}
