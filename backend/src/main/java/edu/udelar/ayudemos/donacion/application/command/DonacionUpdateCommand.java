package edu.udelar.ayudemos.donacion.application.command;

import java.time.LocalDate;

public record DonacionUpdateCommand(
        String numeroIdentificacion,
        LocalDate fechaIngreso,
        String descripcion,
        Integer cantidad,
        Double peso,
        String dimensiones
) {
}
