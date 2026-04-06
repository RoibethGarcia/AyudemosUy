package edu.udelar.ayudemos.beneficiario.api.dto;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;

import java.time.LocalDate;

public record BeneficiarioResponse(
        Long id,
        String nombre,
        String correo,
        String direccion,
        LocalDate fechaNacimiento,
        EstadoBeneficiario estado,
        Barrio barrio
) {
}
