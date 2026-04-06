package edu.udelar.ayudemos.repartidor.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.udelar.ayudemos.common.error.GlobalExceptionHandler;
import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.api.mapper.RepartidorMapper;
import edu.udelar.ayudemos.repartidor.application.RepartidorService;
import edu.udelar.ayudemos.repartidor.application.exception.NumeroLicenciaAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.RepartidorNotFoundException;
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

@WebMvcTest(controllers = RepartidorController.class)
@Import({RepartidorControllerTest.MapperConfig.class, GlobalExceptionHandler.class})
class RepartidorControllerTest {

    @TestConfiguration
    static class MapperConfig {

        @Bean
        RepartidorMapper repartidorMapper() {
            return Mappers.getMapper(RepartidorMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepartidorService repartidorService;

    @Test
    void postRepartidor_devuelve201() throws Exception {
        when(repartidorService.crearRepartidor(any(Repartidor.class)))
                .thenReturn(buildRepartidor(1L, "Juan Perez", "juan@example.com", "LIC-001"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "repartidor123");
        body.put("numeroLicencia", "LIC-001");

        mockMvc.perform(post("/repartidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numeroLicencia").value("LIC-001"));
    }

    @Test
    void getRepartidores_devuelve200() throws Exception {
        when(repartidorService.listarRepartidores()).thenReturn(List.of(
                buildRepartidor(1L, "Juan Perez", "juan@example.com", "LIC-001")
        ));

        mockMvc.perform(get("/repartidores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"))
                .andExpect(jsonPath("$[0].numeroLicencia").value("LIC-001"));

        verify(repartidorService).listarRepartidores();
    }

    @Test
    void getRepartidorPorId_devuelve404SiNoExiste() throws Exception {
        when(repartidorService.obtenerPorId(99L)).thenThrow(new RepartidorNotFoundException(99L));

        mockMvc.perform(get("/repartidores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REPARTIDOR_NOT_FOUND"));
    }

    @Test
    void putRepartidor_devuelve200() throws Exception {
        when(repartidorService.actualizarRepartidor(any(Long.class), any(Repartidor.class)))
                .thenReturn(buildRepartidor(5L, "Ana Gomez", "ana@example.com", "LIC-005"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Ana Gomez");
        body.put("correo", "ana@example.com");
        body.put("numeroLicencia", "LIC-005");

        mockMvc.perform(put("/repartidores/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.correo").value("ana@example.com"));
    }

    @Test
    void postRepartidor_devuelve400CuandoLaValidacionFalla() throws Exception {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "");
        body.put("correo", "correo-invalido");
        body.put("contrasena", "");
        body.put("numeroLicencia", "");

        mockMvc.perform(post("/repartidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.nombre").exists())
                .andExpect(jsonPath("$.details.correo").exists())
                .andExpect(jsonPath("$.details.contrasena").exists())
                .andExpect(jsonPath("$.details.numeroLicencia").exists());
    }

    @Test
    void postRepartidor_devuelve409SiElCorreoEstaDuplicado() throws Exception {
        when(repartidorService.crearRepartidor(any(Repartidor.class)))
                .thenThrow(new EmailAlreadyExistsException("juan@example.com"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "repartidor123");
        body.put("numeroLicencia", "LIC-001");

        mockMvc.perform(post("/repartidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void postRepartidor_devuelve409SiLaLicenciaEstaDuplicada() throws Exception {
        when(repartidorService.crearRepartidor(any(Repartidor.class)))
                .thenThrow(new NumeroLicenciaAlreadyExistsException("LIC-001"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "repartidor123");
        body.put("numeroLicencia", "LIC-001");

        mockMvc.perform(post("/repartidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("NUMERO_LICENCIA_ALREADY_EXISTS"));
    }

    private Repartidor buildRepartidor(
            final Long id,
            final String nombre,
            final String correo,
            final String numeroLicencia
    ) {
        final Repartidor repartidor = new Repartidor();
        repartidor.setId(id);
        repartidor.setNombre(nombre);
        repartidor.setCorreo(correo);
        repartidor.setNumeroLicencia(numeroLicencia);
        return repartidor;
    }
}
