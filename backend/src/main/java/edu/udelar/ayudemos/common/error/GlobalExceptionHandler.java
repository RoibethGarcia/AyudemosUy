package edu.udelar.ayudemos.common.error;

import edu.udelar.ayudemos.beneficiario.application.exception.BeneficiarioNotFoundException;
import edu.udelar.ayudemos.auth.application.exception.InvalidCredentialsException;
import edu.udelar.ayudemos.auth.application.exception.UnauthenticatedSessionException;
import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionBusinessException;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionNotFoundException;
import edu.udelar.ayudemos.donacion.application.exception.DonacionBusinessException;
import edu.udelar.ayudemos.donacion.application.exception.DonacionNotFoundException;
import edu.udelar.ayudemos.donacion.application.exception.NumeroIdentificacionAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.NumeroLicenciaAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.RepartidorNotFoundException;
import edu.udelar.ayudemos.usuario.application.exception.UsuarioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(final MethodArgumentNotValidException exception) {
        final Map<String, String> details = new LinkedHashMap<>();
        for (final FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "La solicitud contiene errores de validacion",
                details
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        final Map<String, String> details = new LinkedHashMap<>();
        details.put("parameter", exception.getName());
        details.put("value", exception.getValue() == null ? "null" : exception.getValue().toString());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "El valor recibido no tiene el formato esperado",
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(final HttpMessageNotReadableException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "El cuerpo de la solicitud no se puede procesar",
                Map.of()
        );
    }

    @ExceptionHandler(BeneficiarioNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBeneficiarioNotFound(final BeneficiarioNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "BENEFICIARIO_NOT_FOUND",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(final EmailAlreadyExistsException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_EXISTS",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(final InvalidCredentialsException exception) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(UnauthenticatedSessionException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthenticatedSession(
            final UnauthenticatedSessionException exception
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHENTICATED",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsuarioNotFound(final UsuarioNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "USUARIO_NOT_FOUND",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(DistribucionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDistribucionNotFound(final DistribucionNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "DISTRIBUCION_NOT_FOUND",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(DonacionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDonacionNotFound(final DonacionNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "DONACION_NOT_FOUND",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(NumeroIdentificacionAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleDonacionAlreadyExists(
            final NumeroIdentificacionAlreadyExistsException exception
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "DONACION_ALREADY_EXISTS",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(DonacionBusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleDonacionBusiness(final DonacionBusinessException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "DONACION_INVALIDA",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(RepartidorNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRepartidorNotFound(final RepartidorNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "REPARTIDOR_NOT_FOUND",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(NumeroLicenciaAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleNumeroLicenciaAlreadyExists(
            final NumeroLicenciaAlreadyExistsException exception
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "NUMERO_LICENCIA_ALREADY_EXISTS",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(DistribucionBusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleDistribucionBusiness(final DistribucionBusinessException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "DISTRIBUCION_INVALIDA",
                exception.getMessage(),
                Map.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            final HttpStatus status,
            final String code,
            final String message,
            final Object details
    ) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(code, message, details, Instant.now()));
    }
}
