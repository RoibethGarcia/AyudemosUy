package edu.udelar.ayudemos.beneficiario.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.udelar.ayudemos.beneficiario.api.mapper.BeneficiarioMapper;
import edu.udelar.ayudemos.beneficiario.application.BeneficiarioService;
import edu.udelar.ayudemos.beneficiario.application.exception.BeneficiarioNotFoundException;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.common.error.GlobalExceptionHandler;
import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
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

@WebMvcTest(controllers = BeneficiarioController.class)
@Import({BeneficiarioControllerTest.MapperConfig.class, GlobalExceptionHandler.class})
class BeneficiarioControllerTest {

    @TestConfiguration
    static class MapperConfig {

        @Bean
        BeneficiarioMapper beneficiarioMapper() {
            return Mappers.getMapper(BeneficiarioMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficiarioService beneficiarioService;

    @Test
    void postBeneficiario_devuelve201() throws Exception {
        final Beneficiario beneficiario = buildBeneficiario(1L);
        when(beneficiarioService.crearBeneficiario(any(Beneficiario.class))).thenReturn(beneficiario);

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "beneficiario123");
        body.put("direccion", "Av. 18 de Julio 1234");
        body.put("fechaNacimiento", "1990-01-15");
        body.put("barrio", "CENTRO");

        mockMvc.perform(post("/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void getBeneficiarios_filtraPorZona() throws Exception {
        when(beneficiarioService.listar(Barrio.CENTRO, null)).thenReturn(List.of(buildBeneficiario(1L)));

        mockMvc.perform(get("/beneficiarios").param("zona", "CENTRO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barrio").value("CENTRO"));

        verify(beneficiarioService).listar(Barrio.CENTRO, null);
    }

    @Test
    void getBeneficiarios_filtraPorEstado() throws Exception {
        when(beneficiarioService.listar(null, EstadoBeneficiario.ACTIVO)).thenReturn(List.of(buildBeneficiario(1L)));

        mockMvc.perform(get("/beneficiarios").param("estado", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));

        verify(beneficiarioService).listar(null, EstadoBeneficiario.ACTIVO);
    }

    @Test
    void getBeneficiarios_filtraPorZonaYEstado() throws Exception {
        when(beneficiarioService.listar(Barrio.CENTRO, EstadoBeneficiario.ACTIVO))
                .thenReturn(List.of(buildBeneficiario(1L)));

        mockMvc.perform(get("/beneficiarios")
                        .param("zona", "CENTRO")
                        .param("estado", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"));

        verify(beneficiarioService).listar(Barrio.CENTRO, EstadoBeneficiario.ACTIVO);
    }

    @Test
    void getBeneficiarioPorId_devuelve404SiNoExiste() throws Exception {
        when(beneficiarioService.obtenerPorId(99L)).thenThrow(new BeneficiarioNotFoundException(99L));

        mockMvc.perform(get("/beneficiarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BENEFICIARIO_NOT_FOUND"));
    }

    @Test
    void putBeneficiario_devuelve200() throws Exception {
        final Beneficiario beneficiario = buildBeneficiario(5L);
        when(beneficiarioService.actualizarBeneficiario(any(Long.class), any(Beneficiario.class)))
                .thenReturn(beneficiario);

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("direccion", "Av. 18 de Julio 1234");
        body.put("fechaNacimiento", "1990-01-15");
        body.put("estado", "ACTIVO");
        body.put("barrio", "CENTRO");

        mockMvc.perform(put("/beneficiarios/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void postBeneficiario_devuelve400CuandoLaValidacionFalla() throws Exception {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "");
        body.put("correo", "correo-invalido");
        body.put("contrasena", "");
        body.put("direccion", "");
        body.put("fechaNacimiento", null);
        body.put("barrio", null);

        mockMvc.perform(post("/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.nombre").exists())
                .andExpect(jsonPath("$.details.correo").exists())
                .andExpect(jsonPath("$.details.contrasena").exists());
    }

    @Test
    void postBeneficiario_devuelve409SiElCorreoEstaDuplicado() throws Exception {
        when(beneficiarioService.crearBeneficiario(any(Beneficiario.class)))
                .thenThrow(new EmailAlreadyExistsException("juan@example.com"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "beneficiario123");
        body.put("direccion", "Av. 18 de Julio 1234");
        body.put("fechaNacimiento", "1990-01-15");
        body.put("barrio", "CENTRO");

        mockMvc.perform(post("/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    private Beneficiario buildBeneficiario(final Long id) {
        final Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(id);
        beneficiario.setNombre("Juan Perez");
        beneficiario.setCorreo("juan@example.com");
        beneficiario.setDireccion("Av. 18 de Julio 1234");
        beneficiario.setFechaNacimiento(LocalDate.of(1990, 1, 15));
        beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        beneficiario.setBarrio(Barrio.CENTRO);
        return beneficiario;
    }
}
