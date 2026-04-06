package edu.udelar.ayudemos.usuario.application;

import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.usuario.application.exception.UsuarioNotFoundException;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearUsuario_hasheaLaContrasenaAntesDePersistir() {
        final Usuario usuario = buildUsuario(1L, "Juan Perez", "juan@example.com");
        usuario.setContrasenaHash("secreto123");

        when(usuarioRepository.findByCorreo("juan@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secreto123")).thenReturn("HASH-123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Usuario creado = usuarioService.crearUsuario(usuario);

        assertThat(creado.getContrasenaHash()).isEqualTo("HASH-123");
    }

    @Test
    void crearUsuario_rechazaCorreoDuplicado() {
        final Usuario usuario = buildUsuario(1L, "Juan Perez", "juan@example.com");
        when(usuarioRepository.findByCorreo("juan@example.com")).thenReturn(Optional.of(buildUsuario(
                99L,
                "Existente",
                "juan@example.com"
        )));

        assertThatThrownBy(() -> usuarioService.crearUsuario(usuario))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("juan@example.com");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void listarUsuarios_consultaSoloUsuariosBase() {
        final List<Usuario> usuarios = List.of(buildUsuario(1L, "Juan Perez", "juan@example.com"));
        when(usuarioRepository.findBaseUsuarios()).thenReturn(usuarios);

        final List<Usuario> resultado = usuarioService.listarUsuarios();

        assertThat(resultado).containsExactlyElementsOf(usuarios);
        verify(usuarioRepository).findBaseUsuarios();
        verify(usuarioRepository, never()).findAll();
    }

    @Test
    void obtenerPorId_lanzaExcepcionEspecificaSiNoExiste() {
        when(usuarioRepository.findBaseUsuarioById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPorId(5L))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessageContaining("5");
    }

    @Test
    void actualizarUsuario_rechazaCorreoDuplicado() {
        final Usuario actual = buildUsuario(10L, "Actual", "actual@example.com");
        final Usuario actualizado = buildUsuario(null, "Nuevo", "duplicado@example.com");

        when(usuarioRepository.findBaseUsuarioById(10L)).thenReturn(Optional.of(actual));
        when(usuarioRepository.findByCorreo("duplicado@example.com")).thenReturn(Optional.of(buildUsuario(
                20L,
                "Duplicado",
                "duplicado@example.com"
        )));

        assertThatThrownBy(() -> usuarioService.actualizarUsuario(10L, actualizado))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("duplicado@example.com");
    }

    @Test
    void actualizarUsuario_persisteCambiosSobreUsuarioBase() {
        final Usuario actual = buildUsuario(10L, "Actual", "actual@example.com");
        final Usuario actualizado = buildUsuario(null, "Nuevo Nombre", "nuevo@example.com");

        when(usuarioRepository.findBaseUsuarioById(10L)).thenReturn(Optional.of(actual));
        when(usuarioRepository.findByCorreo("nuevo@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Usuario resultado = usuarioService.actualizarUsuario(10L, actualizado);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(resultado.getCorreo()).isEqualTo("nuevo@example.com");
    }

    private Usuario buildUsuario(final Long id, final String nombre, final String correo) {
        final Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash("HASH-INICIAL");
        return usuario;
    }
}
