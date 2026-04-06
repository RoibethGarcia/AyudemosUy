package edu.udelar.ayudemos.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.udelar.ayudemos.auth.api.dto.AuthResponse;
import edu.udelar.ayudemos.auth.application.AuthService;
import edu.udelar.ayudemos.auth.application.exception.InvalidCredentialsException;
import edu.udelar.ayudemos.common.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void postLogin_devuelve200() throws Exception {
        when(authService.autenticar(any())).thenReturn(new AuthResponse(1L, "Ana", "ana@ayudemos.uy", "USUARIO"));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("correo", "ana@ayudemos.uy");
        body.put("contrasena", "secreto123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoUsuario").value("USUARIO"));
    }

    @Test
    void postLogin_devuelve401SiLasCredencialesSonInvalidas() throws Exception {
        when(authService.autenticar(any())).thenThrow(new InvalidCredentialsException());

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("correo", "ana@ayudemos.uy");
        body.put("contrasena", "incorrecta");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void getSesion_devuelve401SiNoExisteSesionActiva() throws Exception {
        mockMvc.perform(get("/auth/sesion"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    void getSesion_devuelveLaSesionActual() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute("AUTH_SESSION", new AuthResponse(7L, "Laura", "laura@ayudemos.uy", "REPARTIDOR"));

        mockMvc.perform(get("/auth/sesion").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.tipoUsuario").value("REPARTIDOR"));
    }
}
