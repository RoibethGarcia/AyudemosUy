package edu.udelar.ayudemos.reportes.application;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;

public record ZonaDistribucionReporte(
        Barrio barrio,
        Long cantidadDistribuciones
) {
}
