package edu.udelar.ayudemos.donacion.api.dto;

import edu.udelar.ayudemos.donacion.domain.TipoDonacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DonacionCreateRequest(
        @NotNull(message = "El tipo de donacion es obligatorio")
        TipoDonacion tipo,
        @NotBlank(message = "El numero de identificacion es obligatorio")
        String numeroIdentificacion,
        @NotNull(message = "La fecha de ingreso es obligatoria")
        LocalDate fechaIngreso,
        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion,
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad,
        @Positive(message = "El peso debe ser mayor a cero")
        Double peso,
        String dimensiones
) {
}
