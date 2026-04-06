package edu.udelar.ayudemos.donacion.application;

import edu.udelar.ayudemos.donacion.application.command.DonacionCreateCommand;
import edu.udelar.ayudemos.donacion.application.exception.DonacionBusinessException;
import edu.udelar.ayudemos.donacion.application.exception.NumeroIdentificacionAlreadyExistsException;
import edu.udelar.ayudemos.donacion.domain.Alimento;
import edu.udelar.ayudemos.donacion.domain.Articulo;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.donacion.domain.TipoDonacion;
import edu.udelar.ayudemos.donacion.infrastructure.DonacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DonacionService {

    private final DonacionRepository donacionRepository;

    @Transactional
    public Donacion crearDonacion(final DonacionCreateCommand command) {
        validarNumeroIdentificacionUnico(command.numeroIdentificacion());
        validarCommand(command);

        final Donacion donacion = crearEntidad(command);
        return donacionRepository.save(donacion);
    }

    private void validarNumeroIdentificacionUnico(final String numeroIdentificacion) {
        if (donacionRepository.existsByNumeroIdentificacion(numeroIdentificacion)) {
            throw new NumeroIdentificacionAlreadyExistsException(numeroIdentificacion);
        }
    }

    private void validarCommand(final DonacionCreateCommand command) {
        if (command.tipo() == null) {
            throw new DonacionBusinessException("El tipo de donacion es obligatorio");
        }

        validarValoresPositivos(command);

        if (TipoDonacion.ALIMENTO.equals(command.tipo())) {
            validarAlimento(command);
            return;
        }

        validarArticulo(command);
    }

    private void validarValoresPositivos(final DonacionCreateCommand command) {
        if (command.cantidad() != null && command.cantidad() <= 0) {
            throw new DonacionBusinessException("La cantidad debe ser mayor a cero");
        }

        if (command.peso() != null && command.peso() <= 0) {
            throw new DonacionBusinessException("El peso debe ser mayor a cero");
        }
    }

    private void validarAlimento(final DonacionCreateCommand command) {
        if (command.cantidad() == null) {
            throw new DonacionBusinessException("Un alimento debe indicar cantidad");
        }

        if (command.peso() != null || tieneTexto(command.dimensiones())) {
            throw new DonacionBusinessException("Un alimento no puede indicar peso ni dimensiones");
        }
    }

    private void validarArticulo(final DonacionCreateCommand command) {
        final boolean faltanDatos = command.peso() == null || !tieneTexto(command.dimensiones());
        if (faltanDatos) {
            throw new DonacionBusinessException("Un articulo debe indicar peso y dimensiones");
        }

        if (command.cantidad() != null) {
            throw new DonacionBusinessException("Un articulo no puede indicar cantidad");
        }
    }

    private Donacion crearEntidad(final DonacionCreateCommand command) {
        if (TipoDonacion.ALIMENTO.equals(command.tipo())) {
            return crearAlimento(command);
        }

        return crearArticulo(command);
    }

    private Alimento crearAlimento(final DonacionCreateCommand command) {
        final Alimento alimento = new Alimento();
        alimento.setNumeroIdentificacion(command.numeroIdentificacion());
        alimento.setFechaIngreso(command.fechaIngreso());
        alimento.setDescripcion(command.descripcion());
        alimento.setCantidad(command.cantidad());
        return alimento;
    }

    private Articulo crearArticulo(final DonacionCreateCommand command) {
        final Articulo articulo = new Articulo();
        articulo.setNumeroIdentificacion(command.numeroIdentificacion());
        articulo.setFechaIngreso(command.fechaIngreso());
        articulo.setDescripcion(command.descripcion());
        articulo.setPeso(command.peso());
        articulo.setDimensiones(command.dimensiones());
        return articulo;
    }

    private boolean tieneTexto(final String value) {
        return value != null && !value.isBlank();
    }
}
