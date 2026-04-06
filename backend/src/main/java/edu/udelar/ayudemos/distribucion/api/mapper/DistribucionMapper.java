package edu.udelar.ayudemos.distribucion.api.mapper;

import edu.udelar.ayudemos.distribucion.api.dto.DistribucionCreateRequest;
import edu.udelar.ayudemos.distribucion.api.dto.DistribucionResponse;
import edu.udelar.ayudemos.distribucion.api.dto.DistribucionUpdateRequest;
import edu.udelar.ayudemos.distribucion.application.command.DistribucionCreateCommand;
import edu.udelar.ayudemos.distribucion.application.command.DistribucionUpdateCommand;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DistribucionMapper {

    DistribucionCreateCommand toCreateCommand(DistribucionCreateRequest request);

    DistribucionUpdateCommand toUpdateCommand(DistribucionUpdateRequest request);

    @Mapping(target = "beneficiarioId", source = "beneficiario.id")
    @Mapping(target = "beneficiarioNombre", source = "beneficiario.nombre")
    @Mapping(target = "barrio", source = "beneficiario.barrio")
    @Mapping(target = "donacionIds", source = "donaciones")
    @Mapping(target = "repartidorId", source = "repartidor.id")
    @Mapping(target = "repartidorNombre", source = "repartidor.nombre")
    DistribucionResponse toResponse(Distribucion distribucion);

    default List<Long> mapDonaciones(final List<Donacion> donaciones) {
        if (donaciones == null) {
            return List.of();
        }

        return donaciones.stream()
                .map(Donacion::getId)
                .toList();
    }
}
