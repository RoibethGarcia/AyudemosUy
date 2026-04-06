package edu.udelar.ayudemos.common.error;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        Object details,
        Instant timestamp
) {
}

