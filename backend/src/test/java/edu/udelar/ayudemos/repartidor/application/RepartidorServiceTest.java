package edu.udelar.ayudemos.repartidor.application;

import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.NumeroLicenciaAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.RepartidorNotFoundException;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import edu.udelar.ayudemos.repartidor.infrastructure.RepartidorRepository;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepartidorServiceTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RepartidorService repartidorService;

    @Test
    void crearRepartidor_rechazaCorreoDuplicado() {
        final Repartidor repartidor = buildRepartidor(1L, "Juan Perez", "juan@example.com", "LIC-001");
        final Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(99L);
        usuarioExistente.setCorreo("juan@example.com");

        when(usuarioRepository.findByCorreo("juan@example.com")).thenReturn(Optional.of(usuarioExistente));

        assertThatThrownBy(() -> repartidorService.crearRepartidor(repartidor))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("juan@example.com");

        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }

    @Test
    void crearRepartidor_rechazaNumeroLicenciaDuplicado() {
        final Repartidor repartidor = buildRepartidor(1L, "Juan Perez", "juan@example.com", "LIC-001");

        when(usuarioRepository.findByCorreo("juan@example.com")).thenReturn(Optional.empty());
        when(repartidorRepository.findByNumeroLicencia("LIC-001"))
                .thenReturn(Optional.of(buildRepartidor(88L, "Otro", "otro@example.com", "LIC-001")));

        assertThatThrownBy(() -> repartidorService.crearRepartidor(repartidor))
                .isInstanceOf(NumeroLicenciaAlreadyExistsException.class)
                .hasMessageContaining("LIC-001");
    }

    @Test
    void listarRepartidores_devuelveRepositorioCompleto() {
        final List<Repartidor> expected = List.of(buildRepartidor(1L, "Juan Perez", "juan@example.com", "LIC-001"));
        when(repartidorRepository.findAll()).thenReturn(expected);

        final List<Repartidor> resultado = repartidorService.listarRepartidores();

        assertThat(resultado).containsExactlyElementsOf(expected);
        verify(repartidorRepository).findAll();
    }

    @Test
    void obtenerPorId_lanzaExcepcionEspecificaSiNoExiste() {
        when(repartidorRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repartidorService.obtenerPorId(7L))
                .isInstanceOf(RepartidorNotFoundException.class)
                .hasMessageContaining("7");
    }

    @Test
    void actualizarRepartidor_rechazaCorreoDuplicado() {
        final Repartidor actual = buildRepartidor(10L, "Actual", "actual@example.com", "LIC-010");
        final Repartidor actualizado = buildRepartidor(null, "Nuevo", "duplicado@example.com", "LIC-011");
        final Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(20L);
        usuarioExistente.setCorreo("duplicado@example.com");

        when(repartidorRepository.findById(10L)).thenReturn(Optional.of(actual));
        when(usuarioRepository.findByCorreo("duplicado@example.com")).thenReturn(Optional.of(usuarioExistente));

        assertThatThrownBy(() -> repartidorService.actualizarRepartidor(10L, actualizado))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("duplicado@example.com");
    }

    @Test
    void actualizarRepartidor_rechazaNumeroLicenciaDuplicado() {
        final Repartidor actual = buildRepartidor(10L, "Actual", "actual@example.com", "LIC-010");
        final Repartidor actualizado = buildRepartidor(null, "Nuevo", "nuevo@example.com", "LIC-999");

        when(repartidorRepository.findById(10L)).thenReturn(Optional.of(actual));
        when(usuarioRepository.findByCorreo("nuevo@example.com")).thenReturn(Optional.empty());
        when(repartidorRepository.findByNumeroLicencia("LIC-999"))
                .thenReturn(Optional.of(buildRepartidor(30L, "Otro", "otro@example.com", "LIC-999")));

        assertThatThrownBy(() -> repartidorService.actualizarRepartidor(10L, actualizado))
                .isInstanceOf(NumeroLicenciaAlreadyExistsException.class)
                .hasMessageContaining("LIC-999");
    }

    @Test
    void actualizarRepartidor_persisteCambios() {
        final Repartidor actual = buildRepartidor(10L, "Actual", "actual@example.com", "LIC-010");
        final Repartidor actualizado = buildRepartidor(null, "Nuevo Nombre", "nuevo@example.com", "LIC-011");

        when(repartidorRepository.findById(10L)).thenReturn(Optional.of(actual));
        when(usuarioRepository.findByCorreo("nuevo@example.com")).thenReturn(Optional.empty());
        when(repartidorRepository.findByNumeroLicencia("LIC-011")).thenReturn(Optional.empty());
        when(repartidorRepository.save(any(Repartidor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Repartidor resultado = repartidorService.actualizarRepartidor(10L, actualizado);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(resultado.getCorreo()).isEqualTo("nuevo@example.com");
        assertThat(resultado.getNumeroLicencia()).isEqualTo("LIC-011");
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
