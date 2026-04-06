package edu.udelar.ayudemos.donacion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.udelar.ayudemos.common.error.GlobalExceptionHandler;
import edu.udelar.ayudemos.donacion.api.mapper.DonacionMapper;
import edu.udelar.ayudemos.donacion.application.DonacionService;
import edu.udelar.ayudemos.donacion.application.exception.DonacionBusinessException;
import edu.udelar.ayudemos.donacion.application.exception.DonacionNotFoundException;
import edu.udelar.ayudemos.donacion.application.exception.NumeroIdentificacionAlreadyExistsException;
import edu.udelar.ayudemos.donacion.domain.Alimento;
import edu.udelar.ayudemos.donacion.domain.Articulo;
import edu.udelar.ayudemos.donacion.domain.Donacion;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DonacionController.class)
@Import({DonacionControllerTest.MapperConfig.class, GlobalExceptionHandler.class})
class DonacionControllerTest {

    @TestConfiguration
    static class MapperConfig {

        @Bean
        DonacionMapper donacionMapper() {
            return Mappers.getMapper(DonacionMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DonacionService donacionService;

    @Test
    void postDonacionAlimento_devuelve201() throws Exception {
        when(donacionService.crearDonacion(any())).thenReturn(buildAlimento(1L));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("tipo", "ALIMENTO");
        body.put("numeroIdentificacion", "DON-001");
        body.put("fechaIngreso", "2026-04-01");
        body.put("descripcion", "Arroz");
        body.put("cantidad", 15);

        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipo").value("ALIMENTO"))
                .andExpect(jsonPath("$.cantidad").value(15))
                .andExpect(jsonPath("$.peso").isEmpty());
    }

    @Test
    void postDonacionArticulo_devuelve201() throws Exception {
        when(donacionService.crearDonacion(any())).thenReturn(buildArticulo(2L));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("tipo", "ARTICULO");
        body.put("numeroIdentificacion", "DON-002");
        body.put("fechaIngreso", "2026-04-02");
        body.put("descripcion", "Mesa plegable");
        body.put("peso", 12.5);
        body.put("dimensiones", "120x60x75");

        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.tipo").value("ARTICULO"))
                .andExpect(jsonPath("$.peso").value(12.5))
                .andExpect(jsonPath("$.dimensiones").value("120x60x75"));
    }

    @Test
    void putDonacion_devuelve200() throws Exception {
        when(donacionService.actualizarDonacion(any(Long.class), any())).thenReturn(buildAlimento(5L));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("numeroIdentificacion", "DON-005");
        body.put("fechaIngreso", "2026-04-05");
        body.put("descripcion", "Arroz integral");
        body.put("cantidad", 20);

        mockMvc.perform(put("/donaciones/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.tipo").value("ALIMENTO"));
    }

    @Test
    void putDonacion_devuelve404SiNoExiste() throws Exception {
        when(donacionService.actualizarDonacion(any(Long.class), any()))
                .thenThrow(new DonacionNotFoundException(99L));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("numeroIdentificacion", "DON-099");
        body.put("fechaIngreso", "2026-04-05");
        body.put("descripcion", "Arroz integral");
        body.put("cantidad", 20);

        mockMvc.perform(put("/donaciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("DONACION_NOT_FOUND"));
    }

    @Test
    void postDonacion_devuelve400CuandoLaValidacionFalla() throws Exception {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("tipo", null);
        body.put("numeroIdentificacion", "");
        body.put("fechaIngreso", null);
        body.put("descripcion", "");

        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.tipo").exists())
                .andExpect(jsonPath("$.details.numeroIdentificacion").exists())
                .andExpect(jsonPath("$.details.fechaIngreso").exists())
                .andExpect(jsonPath("$.details.descripcion").exists());
    }

    @Test
    void putDonacion_devuelve400CuandoLaValidacionFalla() throws Exception {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("numeroIdentificacion", "");
        body.put("fechaIngreso", null);
        body.put("descripcion", "");

        mockMvc.perform(put("/donaciones/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.numeroIdentificacion").exists())
                .andExpect(jsonPath("$.details.fechaIngreso").exists())
                .andExpect(jsonPath("$.details.descripcion").exists());
    }

    @Test
    void postDonacion_devuelve400SiLaReglaDeNegocioFalla() throws Exception {
        when(donacionService.crearDonacion(any()))
                .thenThrow(new DonacionBusinessException("Un articulo debe indicar peso y dimensiones"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("tipo", "ARTICULO");
        body.put("numeroIdentificacion", "DON-003");
        body.put("fechaIngreso", "2026-04-03");
        body.put("descripcion", "Mesa");

        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DONACION_INVALIDA"));
    }

    @Test
    void putDonacion_devuelve400SiLaReglaDeNegocioFalla() throws Exception {
        when(donacionService.actualizarDonacion(any(Long.class), any()))
                .thenThrow(new DonacionBusinessException("Un alimento no puede indicar peso ni dimensiones"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("numeroIdentificacion", "DON-005");
        body.put("fechaIngreso", "2026-04-05");
        body.put("descripcion", "Arroz integral");
        body.put("peso", 2.5);

        mockMvc.perform(put("/donaciones/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DONACION_INVALIDA"));
    }

    @Test
    void postDonacion_devuelve409SiElNumeroIdentificacionEstaDuplicado() throws Exception {
        when(donacionService.crearDonacion(any()))
                .thenThrow(new NumeroIdentificacionAlreadyExistsException("DON-001"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("tipo", "ALIMENTO");
        body.put("numeroIdentificacion", "DON-001");
        body.put("fechaIngreso", "2026-04-01");
        body.put("descripcion", "Arroz");
        body.put("cantidad", 15);

        mockMvc.perform(post("/donaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DONACION_ALREADY_EXISTS"));
    }

    private Donacion buildAlimento(final Long id) {
        final Alimento alimento = new Alimento();
        alimento.setId(id);
        alimento.setNumeroIdentificacion("DON-001");
        alimento.setFechaIngreso(LocalDate.of(2026, 4, 1));
        alimento.setDescripcion("Arroz");
        alimento.setCantidad(15);
        return alimento;
    }

    private Donacion buildArticulo(final Long id) {
        final Articulo articulo = new Articulo();
        articulo.setId(id);
        articulo.setNumeroIdentificacion("DON-002");
        articulo.setFechaIngreso(LocalDate.of(2026, 4, 2));
        articulo.setDescripcion("Mesa plegable");
        articulo.setPeso(12.5);
        articulo.setDimensiones("120x60x75");
        return articulo;
    }
}
