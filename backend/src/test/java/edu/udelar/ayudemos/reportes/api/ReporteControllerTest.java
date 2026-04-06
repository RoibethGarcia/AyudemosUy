package edu.udelar.ayudemos.reportes.api;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.reportes.api.mapper.ReporteMapper;
import edu.udelar.ayudemos.reportes.application.ReporteService;
import edu.udelar.ayudemos.reportes.application.ZonaDistribucionReporte;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReporteController.class)
@Import(ReporteControllerTest.MapperConfig.class)
class ReporteControllerTest {

    @TestConfiguration
    static class MapperConfig {

        @Bean
        ReporteMapper reporteMapper() {
            return Mappers.getMapper(ReporteMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    void getZonasMayorDistribuciones_devuelve200ConRanking() throws Exception {
        when(reporteService.obtenerZonasConMayorNumeroDeDistribuciones()).thenReturn(List.of(
                new ZonaDistribucionReporte(Barrio.CENTRO, 5L),
                new ZonaDistribucionReporte(Barrio.CORDON, 3L)
        ));

        mockMvc.perform(get("/reportes/zonas-mayor-distribuciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barrio").value("CENTRO"))
                .andExpect(jsonPath("$[0].cantidadDistribuciones").value(5L))
                .andExpect(jsonPath("$[1].barrio").value("CORDON"));

        verify(reporteService).obtenerZonasConMayorNumeroDeDistribuciones();
    }
}
