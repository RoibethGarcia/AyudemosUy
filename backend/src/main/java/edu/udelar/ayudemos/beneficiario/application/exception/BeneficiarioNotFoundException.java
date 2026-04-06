package edu.udelar.ayudemos.beneficiario.application.exception;

public class BeneficiarioNotFoundException extends RuntimeException {

    public BeneficiarioNotFoundException(final Long id) {
        super("Beneficiario no encontrado con ID: " + id);
    }
}
