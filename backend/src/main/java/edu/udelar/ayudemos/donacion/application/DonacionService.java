package edu.udelar.ayudemos.donacion.application;

import edu.udelar.ayudemos.donacion.application.command.DonacionCreateCommand;
import edu.udelar.ayudemos.donacion.application.command.DonacionUpdateCommand;
import edu.udelar.ayudemos.donacion.application.exception.DonacionBusinessException;
import edu.udelar.ayudemos.donacion.application.exception.DonacionNotFoundException;
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
        validarNumeroIdentificacionUnico(command.numeroIdentificacion(), null);
        validarCommand(command);

        final Donacion donacion = crearEntidad(command);
        return donacionRepository.save(donacion);
    }

    @Transactional(readOnly = true)
    public Donacion obtenerPorId(final Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new DonacionNotFoundException(id));
    }

    @Transactional
    public Donacion actualizarDonacion(final Long id, final DonacionUpdateCommand command) {
        final Donacion donacion = obtenerPorId(id);

        validarNumeroIdentificacionUnico(command.numeroIdentificacion(), donacion.getId());
        validarCommandSegunDonacionActual(command, donacion);

        donacion.setNumeroIdentificacion(command.numeroIdentificacion());
        donacion.setFechaIngreso(command.fechaIngreso());
        actualizarCamposEspecificos(donacion, command);

        return donacionRepository.save(donacion);
    }

    private void validarNumeroIdentificacionUnico(final String numeroIdentificacion, final Long donacionPermitidaId) {
        donacionRepository.findByNumeroIdentificacion(numeroIdentificacion)
                .filter(donacion -> donacionPermitidaId == null || !donacion.getId().equals(donacionPermitidaId))
                .ifPresent(donacion -> {
                    throw new NumeroIdentificacionAlreadyExistsException(numeroIdentificacion);
                });
    }

    private void validarCommand(final DonacionCreateCommand command) {
        if (command.tipo() == null) {
            throw new DonacionBusinessException("El tipo de donacion es obligatorio");
        }

        validarValoresPositivos(command.cantidad(), command.peso());

        if (TipoDonacion.ALIMENTO.equals(command.tipo())) {
            validarAlimento(command.cantidad(), command.peso(), command.dimensiones());
            return;
        }

        validarArticulo(command.cantidad(), command.peso(), command.dimensiones());
    }

    private void validarCommandSegunDonacionActual(final DonacionUpdateCommand command, final Donacion donacion) {
        validarValoresPositivos(command.cantidad(), command.peso());

        if (donacion instanceof Alimento) {
            validarAlimento(command.cantidad(), command.peso(), command.dimensiones());
            return;
        }

        validarArticulo(command.cantidad(), command.peso(), command.dimensiones());
    }

    private void validarValoresPositivos(final Integer cantidad, final Double peso) {
        if (cantidad != null && cantidad <= 0) {
            throw new DonacionBusinessException("La cantidad debe ser mayor a cero");
        }

        if (peso != null && peso <= 0) {
            throw new DonacionBusinessException("El peso debe ser mayor a cero");
        }
    }

    private void validarAlimento(final Integer cantidad, final Double peso, final String dimensiones) {
        if (cantidad == null) {
            throw new DonacionBusinessException("Un alimento debe indicar cantidad");
        }

        if (peso != null || tieneTexto(dimensiones)) {
            throw new DonacionBusinessException("Un alimento no puede indicar peso ni dimensiones");
        }
    }

    private void validarArticulo(final Integer cantidad, final Double peso, final String dimensiones) {
        final boolean faltanDatos = peso == null || !tieneTexto(dimensiones);
        if (faltanDatos) {
            throw new DonacionBusinessException("Un articulo debe indicar peso y dimensiones");
        }

        if (cantidad != null) {
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

    private void actualizarCamposEspecificos(final Donacion donacion, final DonacionUpdateCommand command) {
        if (donacion instanceof final Alimento alimento) {
            alimento.setDescripcion(command.descripcion());
            alimento.setCantidad(command.cantidad());
            return;
        }

        if (donacion instanceof final Articulo articulo) {
            articulo.setDescripcion(command.descripcion());
            articulo.setPeso(command.peso());
            articulo.setDimensiones(command.dimensiones());
        }
    }

    private boolean tieneTexto(final String value) {
        return value != null && !value.isBlank();
    }
}
