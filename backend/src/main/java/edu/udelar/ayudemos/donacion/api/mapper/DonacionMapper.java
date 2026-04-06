package edu.udelar.ayudemos.donacion.api.mapper;

import edu.udelar.ayudemos.donacion.api.dto.DonacionCreateRequest;
import edu.udelar.ayudemos.donacion.api.dto.DonacionResponse;
import edu.udelar.ayudemos.donacion.application.command.DonacionCreateCommand;
import edu.udelar.ayudemos.donacion.domain.Alimento;
import edu.udelar.ayudemos.donacion.domain.Articulo;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.donacion.domain.TipoDonacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DonacionMapper {

    DonacionCreateCommand toCreateCommand(DonacionCreateRequest request);

    @Mapping(target = "tipo", expression = "java(resolveTipo(donacion))")
    @Mapping(target = "descripcion", expression = "java(resolveDescripcion(donacion))")
    @Mapping(target = "cantidad", expression = "java(resolveCantidad(donacion))")
    @Mapping(target = "peso", expression = "java(resolvePeso(donacion))")
    @Mapping(target = "dimensiones", expression = "java(resolveDimensiones(donacion))")
    DonacionResponse toResponse(Donacion donacion);

    default TipoDonacion resolveTipo(final Donacion donacion) {
        if (donacion instanceof Alimento) {
            return TipoDonacion.ALIMENTO;
        }

        return TipoDonacion.ARTICULO;
    }

    default String resolveDescripcion(final Donacion donacion) {
        if (donacion instanceof final Alimento alimento) {
            return alimento.getDescripcion();
        }

        if (donacion instanceof final Articulo articulo) {
            return articulo.getDescripcion();
        }

        return null;
    }

    default Integer resolveCantidad(final Donacion donacion) {
        if (donacion instanceof final Alimento alimento) {
            return alimento.getCantidad();
        }

        return null;
    }

    default Double resolvePeso(final Donacion donacion) {
        if (donacion instanceof final Articulo articulo) {
            return articulo.getPeso();
        }

        return null;
    }

    default String resolveDimensiones(final Donacion donacion) {
        if (donacion instanceof final Articulo articulo) {
            return articulo.getDimensiones();
        }

        return null;
    }
}
