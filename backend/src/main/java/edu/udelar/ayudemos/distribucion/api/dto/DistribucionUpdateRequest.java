package edu.udelar.ayudemos.distribucion.api.dto;

import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DistribucionUpdateRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoDistribucion estado,
        LocalDate fechaEntrega,
        @Positive(message = "El repartidor debe ser valido")
        Long repartidorId
) {
}
