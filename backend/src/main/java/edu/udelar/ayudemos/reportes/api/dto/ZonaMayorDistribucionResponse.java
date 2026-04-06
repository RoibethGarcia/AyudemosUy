package edu.udelar.ayudemos.reportes.api.dto;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;

public record ZonaMayorDistribucionResponse(
        Barrio barrio,
        Long cantidadDistribuciones
) {
}
