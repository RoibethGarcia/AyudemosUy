package edu.udelar.ayudemos.distribucion.application.command;

import java.time.LocalDate;
import java.util.List;

public record DistribucionCreateCommand(
        LocalDate fechaPreparacion,
        Long beneficiarioId,
        List<Long> donacionIds
) {
}
