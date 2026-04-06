package edu.udelar.ayudemos.repartidor.api.mapper;

import edu.udelar.ayudemos.repartidor.api.dto.RepartidorCreateRequest;
import edu.udelar.ayudemos.repartidor.api.dto.RepartidorResponse;
import edu.udelar.ayudemos.repartidor.api.dto.RepartidorUpdateRequest;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepartidorMapper {

    @Mapping(target = "id", ignore = true)
    Repartidor toEntity(RepartidorCreateRequest request);

    @Mapping(target = "id", ignore = true)
    Repartidor toEntity(RepartidorUpdateRequest request);

    RepartidorResponse toResponse(Repartidor repartidor);
}
