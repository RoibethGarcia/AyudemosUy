package edu.udelar.ayudemos.reportes.api.mapper;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.reportes.api.dto.ZonaMayorDistribucionResponse;
import edu.udelar.ayudemos.reportes.application.ZonaDistribucionReporte;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteMapperTest {

    private final ReporteMapper reporteMapper = Mappers.getMapper(ReporteMapper.class);

    @Test
    void toResponse_mapeaBarrioYCantidad() {
        final ZonaDistribucionReporte reporte = new ZonaDistribucionReporte(Barrio.CENTRO, 5L);

        final ZonaMayorDistribucionResponse response = reporteMapper.toResponse(reporte);

        assertThat(response.barrio()).isEqualTo(Barrio.CENTRO);
        assertThat(response.cantidadDistribuciones()).isEqualTo(5L);
    }
}
