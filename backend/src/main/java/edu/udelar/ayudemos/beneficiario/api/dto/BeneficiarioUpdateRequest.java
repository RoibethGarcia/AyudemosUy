package edu.udelar.ayudemos.beneficiario.api.dto;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BeneficiarioUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        String correo,
        @NotBlank(message = "La direccion es obligatoria")
        String direccion,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate fechaNacimiento,
        @NotNull(message = "El estado es obligatorio")
        EstadoBeneficiario estado,
        @NotNull(message = "El barrio es obligatorio")
        Barrio barrio
) {
}
