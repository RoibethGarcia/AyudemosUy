package edu.udelar.ayudemos.donacion.application;

import edu.udelar.ayudemos.donacion.application.command.DonacionCreateCommand;
import edu.udelar.ayudemos.donacion.application.exception.DonacionBusinessException;
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

        when(donacionRepository.existsByNumeroIdentificacion("DON-001")).thenReturn(false);
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

        when(donacionRepository.existsByNumeroIdentificacion("DON-002")).thenReturn(false);
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

        when(donacionRepository.existsByNumeroIdentificacion("DON-001")).thenReturn(true);

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

        when(donacionRepository.existsByNumeroIdentificacion("DON-003")).thenReturn(false);

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

        when(donacionRepository.existsByNumeroIdentificacion("DON-004")).thenReturn(false);

        assertThatThrownBy(() -> donacionService.crearDonacion(command))
                .isInstanceOf(DonacionBusinessException.class)
                .hasMessageContaining("no puede indicar peso ni dimensiones");
    }
}
