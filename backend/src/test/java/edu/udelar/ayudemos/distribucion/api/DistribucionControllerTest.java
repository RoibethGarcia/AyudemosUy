package edu.udelar.ayudemos.distribucion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.common.error.GlobalExceptionHandler;
import edu.udelar.ayudemos.distribucion.api.mapper.DistribucionMapper;
import edu.udelar.ayudemos.distribucion.application.DistribucionService;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionBusinessException;
import edu.udelar.ayudemos.distribucion.application.exception.DistribucionNotFoundException;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DistribucionController.class)
@Import({DistribucionControllerTest.MapperConfig.class, GlobalExceptionHandler.class})
class DistribucionControllerTest {

    @TestConfiguration
    static class MapperConfig {

        @Bean
        DistribucionMapper distribucionMapper() {
            return Mappers.getMapper(DistribucionMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DistribucionService distribucionService;

    @Test
    void postDistribucion_devuelve201() throws Exception {
        when(distribucionService.crearDistribucion(any())).thenReturn(buildDistribucionPendiente(1L));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("fechaPreparacion", "2026-04-01");
        body.put("beneficiarioId", 10L);
        body.put("donacionIds", List.of(100L, 101L));

        mockMvc.perform(post("/distribuciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.beneficiarioId").value(10L))
                .andExpect(jsonPath("$.donacionIds[0]").value(100L));
    }

    @Test
    void getDistribuciones_filtraPorZonaYEstado() throws Exception {
        when(distribucionService.listar(Barrio.CENTRO, EstadoDistribucion.PENDIENTE))
                .thenReturn(List.of(buildDistribucionPendiente(1L)));

        mockMvc.perform(get("/distribuciones")
                        .param("zona", "CENTRO")
                        .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barrio").value("CENTRO"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(distribucionService).listar(Barrio.CENTRO, EstadoDistribucion.PENDIENTE);
    }

    @Test
    void getDistribucionPorId_devuelve404SiNoExiste() throws Exception {
        when(distribucionService.obtenerPorId(99L)).thenThrow(new DistribucionNotFoundException(99L));

        mockMvc.perform(get("/distribuciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("DISTRIBUCION_NOT_FOUND"));
    }

    @Test
    void putDistribucion_devuelve200() throws Exception {
        when(distribucionService.actualizarDistribucion(any(Long.class), any()))
                .thenReturn(buildDistribucionEntregada(5L));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("estado", "ENTREGADO");
        body.put("fechaEntrega", "2026-04-03");

        mockMvc.perform(put("/distribuciones/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.repartidorId").value(40L))
                .andExpect(jsonPath("$.fechaEntrega").value("2026-04-03"));
    }

    @Test
    void postDistribucion_devuelve400CuandoLaValidacionFalla() throws Exception {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("fechaPreparacion", null);
        body.put("beneficiarioId", null);
        body.put("donacionIds", List.of());

        mockMvc.perform(post("/distribuciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.fechaPreparacion").exists())
                .andExpect(jsonPath("$.details.beneficiarioId").exists())
                .andExpect(jsonPath("$.details.donacionIds").exists());
    }

    @Test
    void putDistribucion_devuelve400SiLaReglaDeNegocioFalla() throws Exception {
        when(distribucionService.actualizarDistribucion(any(Long.class), any()))
                .thenThrow(new DistribucionBusinessException("Una distribucion entregada debe registrar fecha de entrega"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("estado", "ENTREGADO");

        mockMvc.perform(put("/distribuciones/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DISTRIBUCION_INVALIDA"));
    }

    private Distribucion buildDistribucionPendiente(final Long id) {
        final Distribucion distribucion = new Distribucion();
        distribucion.setId(id);
        distribucion.setFechaPreparacion(LocalDate.of(2026, 4, 1));
        distribucion.setEstado(EstadoDistribucion.PENDIENTE);
        distribucion.setBeneficiario(buildBeneficiario());
        distribucion.setDonaciones(List.of(buildDonacion(100L), buildDonacion(101L)));
        return distribucion;
    }

    private Distribucion buildDistribucionEntregada(final Long id) {
        final Distribucion distribucion = buildDistribucionPendiente(id);
        distribucion.setEstado(EstadoDistribucion.ENTREGADO);
        distribucion.setFechaEntrega(LocalDate.of(2026, 4, 3));
        distribucion.setRepartidor(buildRepartidor());
        return distribucion;
    }

    private Beneficiario buildBeneficiario() {
        final Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(10L);
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

    private Repartidor buildRepartidor() {
        final Repartidor repartidor = new Repartidor();
        repartidor.setId(40L);
        repartidor.setNombre("Mario Gomez");
        repartidor.setCorreo("mario@example.com");
        repartidor.setNumeroLicencia("LIC-40");
        return repartidor;
    }
}
