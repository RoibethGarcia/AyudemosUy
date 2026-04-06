package edu.udelar.ayudemos.distribucion.application;

import edu.udelar.ayudemos.beneficiario.application.exception.BeneficiarioNotFoundException;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.beneficiario.infrastructure.BeneficiarioRepository;
import edu.udelar.ayudemos.distribucion.application.command.DistribucionCreateCommand;
import edu.udelar.ayudemos.distribucion.application.command.DistribucionUpdateCommand;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionBusinessException;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionNotFoundException;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
import edu.udelar.ayudemos.distribucion.infrastructure.DistribucionRepository;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.donacion.infrastructure.DonacionRepository;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import edu.udelar.ayudemos.repartidor.infrastructure.RepartidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistribucionServiceTest {

    @Mock
    private DistribucionRepository distribucionRepository;

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @Mock
    private DonacionRepository donacionRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private DistribucionService distribucionService;

    @Test
    void crearDistribucion_asignaPendienteYResuelveRelaciones() {
        final DistribucionCreateCommand command = new DistribucionCreateCommand(
                LocalDate.of(2026, 4, 1),
                10L,
                List.of(100L, 101L)
        );
        final Beneficiario beneficiario = buildBeneficiario(10L);
        final List<Donacion> donaciones = List.of(buildDonacion(100L), buildDonacion(101L));

        when(beneficiarioRepository.findById(10L)).thenReturn(Optional.of(beneficiario));
        when(donacionRepository.findAllById(List.of(100L, 101L))).thenReturn(donaciones);
        when(distribucionRepository.save(any(Distribucion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Distribucion creada = distribucionService.crearDistribucion(command);

        final ArgumentCaptor<Distribucion> captor = ArgumentCaptor.forClass(Distribucion.class);
        verify(distribucionRepository).save(captor.capture());
        assertThat(creada.getEstado()).isEqualTo(EstadoDistribucion.PENDIENTE);
        assertThat(creada.getRepartidor()).isNull();
        assertThat(creada.getFechaEntrega()).isNull();
        assertThat(creada.getDonaciones()).containsExactlyElementsOf(donaciones);
        assertThat(captor.getValue().getBeneficiario()).isEqualTo(beneficiario);
    }

    @Test
    void crearDistribucion_rechazaDonacionesDuplicadas() {
        final DistribucionCreateCommand command = new DistribucionCreateCommand(
                LocalDate.of(2026, 4, 1),
                10L,
                List.of(100L, 100L)
        );

        assertThatThrownBy(() -> distribucionService.crearDistribucion(command))
                .isInstanceOf(DistribucionBusinessException.class)
                .hasMessageContaining("repetir donaciones");

        verify(beneficiarioRepository, never()).findById(any(Long.class));
        verify(donacionRepository, never()).findAllById(any());
    }

    @Test
    void listar_usaFiltroCombinadoSinCargarTodoEnMemoria() {
        final List<Distribucion> expected = List.of(buildDistribucionPendiente(1L));
        when(distribucionRepository.findByBeneficiarioBarrioAndEstado(Barrio.CENTRO, EstadoDistribucion.PENDIENTE))
                .thenReturn(expected);

        final List<Distribucion> resultado = distribucionService.listar(Barrio.CENTRO, EstadoDistribucion.PENDIENTE);

        assertThat(resultado).containsExactlyElementsOf(expected);
        verify(distribucionRepository).findByBeneficiarioBarrioAndEstado(Barrio.CENTRO, EstadoDistribucion.PENDIENTE);
        verify(distribucionRepository, never()).findAll();
    }

    @Test
    void actualizarDistribucion_exigeRepartidorCuandoPasaAEnCamino() {
        final Distribucion distribucion = buildDistribucionPendiente(5L);
        final DistribucionUpdateCommand command = new DistribucionUpdateCommand(
                EstadoDistribucion.EN_CAMINO,
                null,
                null
        );

        when(distribucionRepository.findById(5L)).thenReturn(Optional.of(distribucion));

        assertThatThrownBy(() -> distribucionService.actualizarDistribucion(5L, command))
                .isInstanceOf(DistribucionBusinessException.class)
                .hasMessageContaining("repartidor");
    }

    @Test
    void actualizarDistribucion_conservaRepartidorExistenteAlMarcarEntregada() {
        final Distribucion distribucion = buildDistribucionPendiente(5L);
        distribucion.setEstado(EstadoDistribucion.EN_CAMINO);
        distribucion.setRepartidor(buildRepartidor(40L));

        final DistribucionUpdateCommand command = new DistribucionUpdateCommand(
                EstadoDistribucion.ENTREGADO,
                LocalDate.of(2026, 4, 3),
                null
        );

        when(distribucionRepository.findById(5L)).thenReturn(Optional.of(distribucion));
        when(distribucionRepository.save(any(Distribucion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Distribucion actualizada = distribucionService.actualizarDistribucion(5L, command);

        assertThat(actualizada.getEstado()).isEqualTo(EstadoDistribucion.ENTREGADO);
        assertThat(actualizada.getFechaEntrega()).isEqualTo(LocalDate.of(2026, 4, 3));
        assertThat(actualizada.getRepartidor()).isNotNull();
        assertThat(actualizada.getRepartidor().getId()).isEqualTo(40L);
    }

    @Test
    void obtenerPorId_lanzaExcepcionEspecificaSiNoExiste() {
        when(distribucionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> distribucionService.obtenerPorId(99L))
                .isInstanceOf(DistribucionNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crearDistribucion_rechazaBeneficiarioInexistente() {
        final DistribucionCreateCommand command = new DistribucionCreateCommand(
                LocalDate.of(2026, 4, 1),
                99L,
                List.of(100L)
        );

        when(beneficiarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> distribucionService.crearDistribucion(command))
                .isInstanceOf(BeneficiarioNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Distribucion buildDistribucionPendiente(final Long id) {
        final Distribucion distribucion = new Distribucion();
        distribucion.setId(id);
        distribucion.setFechaPreparacion(LocalDate.of(2026, 4, 1));
        distribucion.setEstado(EstadoDistribucion.PENDIENTE);
        distribucion.setBeneficiario(buildBeneficiario(10L));
        distribucion.setDonaciones(List.of(buildDonacion(100L)));
        return distribucion;
    }

    private Beneficiario buildBeneficiario(final Long id) {
        final Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(id);
        beneficiario.setNombre("Ana Perez");
        beneficiario.setCorreo("ana@example.com");
        beneficiario.setDireccion("Av. Rivera 123");
        beneficiario.setFechaNacimiento(LocalDate.of(1992, 6, 10));
        beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        beneficiario.setBarrio(Barrio.CENTRO);
        return beneficiario;
    }

    private Donacion buildDonacion(final Long id) {
        final Donacion donacion = new Donacion();
        donacion.setId(id);
        donacion.setNumeroIdentificacion("DON-" + id);
        donacion.setFechaIngreso(LocalDate.of(2026, 3, 30));
        return donacion;
    }

    private Repartidor buildRepartidor(final Long id) {
        final Repartidor repartidor = new Repartidor();
        repartidor.setId(id);
        repartidor.setNombre("Mario Gomez");
        repartidor.setCorreo("mario@example.com");
        repartidor.setNumeroLicencia("LIC-" + id);
        return repartidor;
    }
}
