package edu.udelar.ayudemos.distribucion.api.dto;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;

import java.time.LocalDate;
import java.util.List;

public record DistribucionResponse(
        Long id,
        LocalDate fechaPreparacion,
        LocalDate fechaEntrega,
        EstadoDistribucion estado,
        Long beneficiarioId,
        String beneficiarioNombre,
        Barrio barrio,
        List<Long> donacionIds,
        Long repartidorId,
        String repartidorNombre
) {
}
