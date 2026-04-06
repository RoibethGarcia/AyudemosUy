package edu.udelar.ayudemos.auth.application;

import edu.udelar.ayudemos.auth.api.dto.AuthResponse;
import edu.udelar.ayudemos.auth.api.dto.LoginRequest;
import edu.udelar.ayudemos.auth.application.exception.InvalidCredentialsException;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void autenticar_devuelveTipoUsuarioSegunEntidad() {
        final Repartidor repartidor = new Repartidor();
        repartidor.setId(7L);
        repartidor.setNombre("Laura");
        repartidor.setCorreo("laura@ayudemos.uy");
        repartidor.setContrasenaHash("HASH");
        repartidor.setNumeroLicencia("LIC-007");

        when(usuarioRepository.findByCorreo("laura@ayudemos.uy")).thenReturn(Optional.of(repartidor));
        when(passwordEncoder.matches("secreto123", "HASH")).thenReturn(true);

        final AuthResponse response = authService.autenticar(new LoginRequest("laura@ayudemos.uy", "secreto123"));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.tipoUsuario()).isEqualTo("REPARTIDOR");
    }

    @Test
    void autenticar_rechazaCorreoDesconocido() {
        when(usuarioRepository.findByCorreo("nadie@ayudemos.uy")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.autenticar(new LoginRequest("nadie@ayudemos.uy", "secreto123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void autenticar_rechazaContrasenaIncorrecta() {
        final Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setNombre("Juan");
        usuario.setCorreo("juan@ayudemos.uy");
        usuario.setContrasenaHash("HASH");

        when(usuarioRepository.findByCorreo("juan@ayudemos.uy")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("otra", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> authService.autenticar(new LoginRequest("juan@ayudemos.uy", "otra")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
