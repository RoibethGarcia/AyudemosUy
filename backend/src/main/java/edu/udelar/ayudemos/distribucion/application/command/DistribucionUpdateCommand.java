package edu.udelar.ayudemos.distribucion.application.command;

import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;

import java.time.LocalDate;

public record DistribucionUpdateCommand(
        EstadoDistribucion estado,
        LocalDate fechaEntrega,
        Long repartidorId
) {
}
