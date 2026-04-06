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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonacionServiceTest {

    @Mock
    private DonacionRepository donacionRepository;

    @InjectMocks
    private DonacionService donacionService;

    @Test
    void crearDonacion_creaAlimentoYPersisteElSubtipoCorrecto() {
        final DonacionCreateCommand command = new DonacionCreateCommand(
                TipoDonacion.ALIMENTO,
                "DON-001",
                LocalDate.of(2026, 4, 1),
                "Arroz",
                15,
                null,
                null
        );

        when(donacionRepository.findByNumeroIdentificacion("DON-001")).thenReturn(Optional.empty());
        when(donacionRepository.save(any(Donacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Donacion creada = donacionService.crearDonacion(command);

        final ArgumentCaptor<Donacion> captor = ArgumentCaptor.forClass(Donacion.class);
        verify(donacionRepository).save(captor.capture());
        assertThat(creada).isInstanceOf(Alimento.class);
        assertThat(captor.getValue()).isInstanceOf(Alimento.class);
        assertThat(((Alimento) captor.getValue()).getCantidad()).isEqualTo(15);
    }

    @Test
    void crearDonacion_creaArticuloYPersisteElSubtipoCorrecto() {
        final DonacionCreateCommand command = new DonacionCreateCommand(
                TipoDonacion.ARTICULO,
                "DON-002",
                LocalDate.of(2026, 4, 2),
                "Mesa plegable",
                null,
                12.5,
                "120x60x75"
        );

        when(donacionRepository.findByNumeroIdentificacion("DON-002")).thenReturn(Optional.empty());
        when(donacionRepository.save(any(Donacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Donacion creada = donacionService.crearDonacion(command);

        final ArgumentCaptor<Donacion> captor = ArgumentCaptor.forClass(Donacion.class);
        verify(donacionRepository).save(captor.capture());
        assertThat(creada).isInstanceOf(Articulo.class);
        assertThat(captor.getValue()).isInstanceOf(Articulo.class);
        assertThat(((Articulo) captor.getValue()).getPeso()).isEqualTo(12.5);
        assertThat(((Articulo) captor.getValue()).getDimensiones()).isEqualTo("120x60x75");
    }

    @Test
    void crearDonacion_rechazaNumeroIdentificacionDuplicado() {
        final DonacionCreateCommand command = new DonacionCreateCommand(
                TipoDonacion.ALIMENTO,
                "DON-001",
                LocalDate.of(2026, 4, 1),
                "Arroz",
                15,
                null,
                null
        );

        when(donacionRepository.findByNumeroIdentificacion("DON-001"))
                .thenReturn(Optional.of(buildAlimento(9L, "DON-001")));

        assertThatThrownBy(() -> donacionService.crearDonacion(command))
                .isInstanceOf(NumeroIdentificacionAlreadyExistsException.class)
                .hasMessageContaining("DON-001");

        verify(donacionRepository, never()).save(any(Donacion.class));
    }

    @Test
    void crearDonacion_rechazaArticuloSinPesoODimensiones() {
        final DonacionCreateCommand command = new DonacionCreateCommand(
                TipoDonacion.ARTICULO,
                "DON-003",
                LocalDate.of(2026, 4, 3),
                "Mesa",
                null,
                null,
                null
        );

        when(donacionRepository.findByNumeroIdentificacion("DON-003")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> donacionService.crearDonacion(command))
                .isInstanceOf(DonacionBusinessException.class)
                .hasMessageContaining("peso y dimensiones");
    }

    @Test
    void crearDonacion_rechazaAlimentoConCamposDeArticulo() {
        final DonacionCreateCommand command = new DonacionCreateCommand(
                TipoDonacion.ALIMENTO,
                "DON-004",
                LocalDate.of(2026, 4, 4),
                "Fideos",
                20,
                5.0,
                "30x20x10"
        );

        when(donacionRepository.findByNumeroIdentificacion("DON-004")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> donacionService.crearDonacion(command))
                .isInstanceOf(DonacionBusinessException.class)
                .hasMessageContaining("no puede indicar peso ni dimensiones");
    }

    @Test
    void actualizarDonacion_modificaAlimentoManteniendoSuSubtipo() {
        final Alimento actual = buildAlimento(5L, "DON-010");
        final DonacionUpdateCommand command = new DonacionUpdateCommand(
                "DON-010-EDIT",
                LocalDate.of(2026, 4, 10),
                "Arroz integral",
                22,
                null,
                null
        );

        when(donacionRepository.findById(5L)).thenReturn(Optional.of(actual));
        when(donacionRepository.findByNumeroIdentificacion("DON-010-EDIT")).thenReturn(Optional.empty());
        when(donacionRepository.save(any(Donacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Donacion actualizada = donacionService.actualizarDonacion(5L, command);

        assertThat(actualizada).isInstanceOf(Alimento.class);
        assertThat(actualizada.getNumeroIdentificacion()).isEqualTo("DON-010-EDIT");
        assertThat(actualizada.getFechaIngreso()).isEqualTo(LocalDate.of(2026, 4, 10));
        assertThat(((Alimento) actualizada).getDescripcion()).isEqualTo("Arroz integral");
        assertThat(((Alimento) actualizada).getCantidad()).isEqualTo(22);
    }

    @Test
    void actualizarDonacion_rechazaCambiarUnArticuloConDatosDeAlimento() {
        final Articulo actual = buildArticulo(8L, "DON-020");
        final DonacionUpdateCommand command = new DonacionUpdateCommand(
                "DON-020",
                LocalDate.of(2026, 4, 12),
                "Mesa editada",
                3,
                null,
                null
        );

        when(donacionRepository.findById(8L)).thenReturn(Optional.of(actual));
        when(donacionRepository.findByNumeroIdentificacion("DON-020")).thenReturn(Optional.of(actual));

        assertThatThrownBy(() -> donacionService.actualizarDonacion(8L, command))
                .isInstanceOf(DonacionBusinessException.class)
                .hasMessageContaining("peso y dimensiones");
    }

    @Test
    void actualizarDonacion_rechazaNumeroIdentificacionDuplicado() {
        final Alimento actual = buildAlimento(5L, "DON-010");
        final DonacionUpdateCommand command = new DonacionUpdateCommand(
                "DON-099",
                LocalDate.of(2026, 4, 10),
                "Arroz integral",
                22,
                null,
                null
        );

        when(donacionRepository.findById(5L)).thenReturn(Optional.of(actual));
        when(donacionRepository.findByNumeroIdentificacion("DON-099"))
                .thenReturn(Optional.of(buildAlimento(99L, "DON-099")));

        assertThatThrownBy(() -> donacionService.actualizarDonacion(5L, command))
                .isInstanceOf(NumeroIdentificacionAlreadyExistsException.class)
                .hasMessageContaining("DON-099");
    }

    @Test
    void obtenerPorId_lanzaExcepcionEspecificaSiNoExiste() {
        when(donacionRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> donacionService.obtenerPorId(77L))
                .isInstanceOf(DonacionNotFoundException.class)
                .hasMessageContaining("77");
    }

    private Alimento buildAlimento(final Long id, final String numeroIdentificacion) {
        final Alimento alimento = new Alimento();
        alimento.setId(id);
        alimento.setNumeroIdentificacion(numeroIdentificacion);
        alimento.setFechaIngreso(LocalDate.of(2026, 4, 1));
        alimento.setDescripcion("Arroz");
        alimento.setCantidad(15);
        return alimento;
    }

    private Articulo buildArticulo(final Long id, final String numeroIdentificacion) {
        final Articulo articulo = new Articulo();
        articulo.setId(id);
        articulo.setNumeroIdentificacion(numeroIdentificacion);
        articulo.setFechaIngreso(LocalDate.of(2026, 4, 2));
        articulo.setDescripcion("Mesa plegable");
        articulo.setPeso(12.5);
        articulo.setDimensiones("120x60x75");
        return articulo;
    }
}
