package edu.udelar.ayudemos.reportes.api.mapper;

import edu.udelar.ayudemos.reportes.api.dto.ZonaMayorDistribucionResponse;
import edu.udelar.ayudemos.reportes.application.ZonaDistribucionReporte;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReporteMapper {

    ZonaMayorDistribucionResponse toResponse(ZonaDistribucionReporte reporte);
}
