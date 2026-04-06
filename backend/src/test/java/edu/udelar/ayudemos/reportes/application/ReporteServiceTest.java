package edu.udelar.ayudemos.reportes.application;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.distribucion.infrastructure.DistribucionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private DistribucionRepository distribucionRepository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void obtenerZonasConMayorNumeroDeDistribuciones_devuelveRankingDescendente() {
        final List<ZonaDistribucionReporte> ranking = List.of(
                new ZonaDistribucionReporte(Barrio.CENTRO, 5L),
                new ZonaDistribucionReporte(Barrio.CORDON, 3L)
        );
        when(distribucionRepository.obtenerCantidadDistribucionesPorBarrio()).thenReturn(ranking);

        final List<ZonaDistribucionReporte> resultado = reporteService.obtenerZonasConMayorNumeroDeDistribuciones();

        assertThat(resultado).containsExactlyElementsOf(ranking);
        verify(distribucionRepository).obtenerCantidadDistribucionesPorBarrio();
    }
}
