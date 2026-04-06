package edu.udelar.ayudemos.distribucion.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record DistribucionCreateRequest(
        @NotNull(message = "La fecha de preparacion es obligatoria")
        LocalDate fechaPreparacion,
        @NotNull(message = "El beneficiario es obligatorio")
        @Positive(message = "El beneficiario debe ser valido")
        Long beneficiarioId,
        @NotEmpty(message = "La distribucion debe incluir al menos una donacion")
        List<
                @NotNull(message = "Cada donacion es obligatoria")
                @Positive(message = "Cada donacion debe ser valida")
                Long> donacionIds
) {
}
