package edu.udelar.ayudemos.distribucion.application;

import edu.udelar.ayudemos.beneficiario.application.exception.BeneficiarioNotFoundException;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.infrastructure.BeneficiarioRepository;
import edu.udelar.ayudemos.distribucion.application.command.DistribucionCreateCommand;
import edu.udelar.ayudemos.distribucion.application.command.DistribucionUpdateCommand;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionBusinessException;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionNotFoundException;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
import edu.udelar.ayudemos.distribucion.infrastructure.DistribucionRepository;
import edu.udelar.ayudemos.donacion.application.exception.DonacionNotFoundException;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.donacion.infrastructure.DonacionRepository;
import edu.udelar.ayudemos.repartidor.application.exception.RepartidorNotFoundException;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import edu.udelar.ayudemos.repartidor.infrastructure.RepartidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistribucionService {

    private final DistribucionRepository distribucionRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final DonacionRepository donacionRepository;
    private final RepartidorRepository repartidorRepository;

    @Transactional
    public Distribucion crearDistribucion(final DistribucionCreateCommand command) {
        validarIdsDonaciones(command.donacionIds());

        final Beneficiario beneficiario = beneficiarioRepository.findById(command.beneficiarioId())
                .orElseThrow(() -> new BeneficiarioNotFoundException(command.beneficiarioId()));
        final List<Donacion> donaciones = obtenerDonaciones(command.donacionIds());

        final Distribucion distribucion = new Distribucion();
        distribucion.setFechaPreparacion(command.fechaPreparacion());
        distribucion.setEstado(EstadoDistribucion.PENDIENTE);
        distribucion.setBeneficiario(beneficiario);
        distribucion.setDonaciones(donaciones);
        distribucion.setFechaEntrega(null);
        distribucion.setRepartidor(null);

        validarDistribucion(distribucion);

        return distribucionRepository.save(distribucion);
    }

    @Transactional(readOnly = true)
    public List<Distribucion> listar(final Barrio zona, final EstadoDistribucion estado) {
        if (zona != null && estado != null) {
            return distribucionRepository.findByBeneficiarioBarrioAndEstado(zona, estado);
        }
        if (zona != null) {
            return distribucionRepository.findByBeneficiarioBarrio(zona);
        }
        if (estado != null) {
            return distribucionRepository.findByEstado(estado);
        }
        return distribucionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Distribucion obtenerPorId(final Long id) {
        return distribucionRepository.findById(id)
                .orElseThrow(() -> new DistribucionNotFoundException(id));
    }

    @Transactional
    public Distribucion actualizarDistribucion(final Long id, final DistribucionUpdateCommand command) {
        final Distribucion distribucion = obtenerPorId(id);

        distribucion.setEstado(command.estado());
        distribucion.setRepartidor(resolverRepartidorParaActualizacion(distribucion, command.repartidorId()));
        distribucion.setFechaEntrega(resolverFechaEntregaParaActualizacion(distribucion, command.fechaEntrega()));

        validarDistribucion(distribucion);

        return distribucionRepository.save(distribucion);
    }

    private Repartidor resolverRepartidorParaActualizacion(final Distribucion distribucion, final Long repartidorId) {
        if (EstadoDistribucion.PENDIENTE.equals(distribucion.getEstado())) {
            if (repartidorId != null) {
                throw new DistribucionBusinessException("Una distribucion pendiente no puede tener repartidor asignado");
            }
            return null;
        }

        if (repartidorId != null) {
            return repartidorRepository.findById(repartidorId)
                    .orElseThrow(() -> new RepartidorNotFoundException(repartidorId));
        }

        return distribucion.getRepartidor();
    }

    private LocalDate resolverFechaEntregaParaActualizacion(
            final Distribucion distribucion,
            final LocalDate fechaEntrega
    ) {
        if (EstadoDistribucion.ENTREGADO.equals(distribucion.getEstado())) {
            if (fechaEntrega != null) {
                return fechaEntrega;
            }
            return distribucion.getFechaEntrega();
        }

        if (fechaEntrega != null) {
            throw new DistribucionBusinessException("Solo una distribucion entregada puede registrar fecha de entrega");
        }

        return null;
    }

    private List<Donacion> obtenerDonaciones(final List<Long> donacionIds) {
        final List<Long> idsNormalizados = new ArrayList<>(new LinkedHashSet<>(donacionIds));
        final List<Donacion> donaciones = donacionRepository.findAllById(idsNormalizados);
        final Set<Long> idsEncontrados = donaciones.stream()
                .map(Donacion::getId)
                .collect(Collectors.toSet());

        final List<Long> idsFaltantes = idsNormalizados.stream()
                .filter(id -> !idsEncontrados.contains(id))
                .toList();

        if (!idsFaltantes.isEmpty()) {
            throw new DonacionNotFoundException(idsFaltantes);
        }

        return donaciones;
    }

    private void validarIdsDonaciones(final List<Long> donacionIds) {
        if (donacionIds == null || donacionIds.isEmpty()) {
            throw new DistribucionBusinessException("Una distribucion debe incluir al menos una donacion");
        }

        final Set<Long> idsUnicos = new LinkedHashSet<>(donacionIds);
        if (idsUnicos.size() != donacionIds.size()) {
            throw new DistribucionBusinessException("Una distribucion no puede repetir donaciones");
        }
    }

    private void validarDistribucion(final Distribucion distribucion) {
        validarRepartidorSegunEstado(distribucion);
        validarFechaEntregaSegunEstado(distribucion);
        validarCronologia(distribucion);
    }

    private void validarRepartidorSegunEstado(final Distribucion distribucion) {
        if (EstadoDistribucion.PENDIENTE.equals(distribucion.getEstado()) && distribucion.getRepartidor() != null) {
            throw new DistribucionBusinessException("Una distribucion pendiente no puede tener repartidor asignado");
        }

        final boolean requiereRepartidor = EstadoDistribucion.EN_CAMINO.equals(distribucion.getEstado())
                || EstadoDistribucion.ENTREGADO.equals(distribucion.getEstado());
        if (requiereRepartidor && distribucion.getRepartidor() == null) {
            throw new DistribucionBusinessException(
                    "Las distribuciones en camino o entregadas requieren un repartidor asignado"
            );
        }
    }

    private void validarFechaEntregaSegunEstado(final Distribucion distribucion) {
        if (EstadoDistribucion.ENTREGADO.equals(distribucion.getEstado()) && distribucion.getFechaEntrega() == null) {
            throw new DistribucionBusinessException("Una distribucion entregada debe registrar fecha de entrega");
        }

        final boolean fechaEntregaNoPermitida = !EstadoDistribucion.ENTREGADO.equals(distribucion.getEstado())
                && distribucion.getFechaEntrega() != null;
        if (fechaEntregaNoPermitida) {
            throw new DistribucionBusinessException("Solo una distribucion entregada puede registrar fecha de entrega");
        }
    }

    private void validarCronologia(final Distribucion distribucion) {
        final LocalDate fechaEntrega = distribucion.getFechaEntrega();
        if (fechaEntrega == null) {
            return;
        }

        if (fechaEntrega.isBefore(distribucion.getFechaPreparacion())) {
            throw new DistribucionBusinessException(
                    "La fecha de entrega no puede ser anterior a la fecha de preparacion"
            );
        }
    }
}
