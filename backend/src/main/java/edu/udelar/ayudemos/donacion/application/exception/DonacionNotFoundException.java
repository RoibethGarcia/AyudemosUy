package edu.udelar.ayudemos.donacion.application.exception;

import java.util.List;

public class DonacionNotFoundException extends RuntimeException {

    public DonacionNotFoundException(final List<Long> ids) {
        super("No existen donaciones para los ids " + ids);
    }
}
