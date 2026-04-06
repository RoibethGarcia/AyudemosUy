package edu.udelar.ayudemos.donacion.api.dto;

import edu.udelar.ayudemos.donacion.domain.TipoDonacion;

import java.time.LocalDate;

public record DonacionResponse(
        Long id,
        TipoDonacion tipo,
        String numeroIdentificacion,
        LocalDate fechaIngreso,
        String descripcion,
        Integer cantidad,
        Double peso,
        String dimensiones
) {
}
