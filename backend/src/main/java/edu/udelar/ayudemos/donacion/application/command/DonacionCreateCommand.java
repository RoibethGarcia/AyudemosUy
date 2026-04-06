package edu.udelar.ayudemos.donacion.application.command;

import edu.udelar.ayudemos.donacion.domain.TipoDonacion;

import java.time.LocalDate;

public record DonacionCreateCommand(
        TipoDonacion tipo,
        String numeroIdentificacion,
        LocalDate fechaIngreso,
        String descripcion,
        Integer cantidad,
        Double peso,
        String dimensiones
) {
}
