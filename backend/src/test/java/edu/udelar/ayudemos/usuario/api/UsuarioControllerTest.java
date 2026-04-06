package edu.udelar.ayudemos.usuario.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.udelar.ayudemos.common.error.GlobalExceptionHandler;
import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.usuario.api.mapper.UsuarioMapper;
import edu.udelar.ayudemos.usuario.application.UsuarioService;
import edu.udelar.ayudemos.usuario.application.exception.UsuarioNotFoundException;
import edu.udelar.ayudemos.usuario.domain.Usuario;
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

@WebMvcTest(controllers = UsuarioController.class)
@Import({UsuarioControllerTest.MapperConfig.class, GlobalExceptionHandler.class})
class UsuarioControllerTest {

    @TestConfiguration
    static class MapperConfig {

        @Bean
        UsuarioMapper usuarioMapper() {
            return Mappers.getMapper(UsuarioMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void postUsuario_devuelve201() throws Exception {
        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(buildUsuario(1L, "Juan Perez", "juan@example.com"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "secreto123");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.correo").value("juan@example.com"));
    }

    @Test
    void getUsuarios_devuelve200() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(buildUsuario(1L, "Juan Perez", "juan@example.com")));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"));

        verify(usuarioService).listarUsuarios();
    }

    @Test
    void getUsuarioPorId_devuelve404SiNoExiste() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenThrow(new UsuarioNotFoundException(99L));

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USUARIO_NOT_FOUND"));
    }

    @Test
    void putUsuario_devuelve200() throws Exception {
        when(usuarioService.actualizarUsuario(any(Long.class), any(Usuario.class)))
                .thenReturn(buildUsuario(5L, "Ana Gomez", "ana@example.com"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Ana Gomez");
        body.put("correo", "ana@example.com");

        mockMvc.perform(put("/usuarios/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.nombre").value("Ana Gomez"));
    }

    @Test
    void postUsuario_devuelve400CuandoLaValidacionFalla() throws Exception {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "");
        body.put("correo", "correo-invalido");
        body.put("contrasena", "");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.nombre").exists())
                .andExpect(jsonPath("$.details.correo").exists())
                .andExpect(jsonPath("$.details.contrasena").exists());
    }

    @Test
    void postUsuario_devuelve409SiElCorreoEstaDuplicado() throws Exception {
        when(usuarioService.crearUsuario(any(Usuario.class)))
                .thenThrow(new EmailAlreadyExistsException("juan@example.com"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("nombre", "Juan Perez");
        body.put("correo", "juan@example.com");
        body.put("contrasena", "secreto123");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    private Usuario buildUsuario(final Long id, final String nombre, final String correo) {
        final Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        return usuario;
    }
}
