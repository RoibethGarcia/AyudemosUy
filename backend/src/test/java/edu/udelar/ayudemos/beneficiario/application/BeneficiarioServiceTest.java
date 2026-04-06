package edu.udelar.ayudemos.beneficiario.application;

import edu.udelar.ayudemos.beneficiario.application.exception.BeneficiarioNotFoundException;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.beneficiario.infrastructure.BeneficiarioRepository;
import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    @Test
    void crearBeneficiario_asignaEstadoActivoPorDefecto() {
        final Beneficiario beneficiario = buildBeneficiario();
        beneficiario.setEstado(null);

        when(usuarioRepository.findByCorreo(beneficiario.getCorreo())).thenReturn(Optional.empty());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Beneficiario creado = beneficiarioService.crearBeneficiario(beneficiario);

        final ArgumentCaptor<Beneficiario> captor = ArgumentCaptor.forClass(Beneficiario.class);
        verify(beneficiarioRepository).save(captor.capture());
        assertThat(creado.getEstado()).isEqualTo(EstadoBeneficiario.ACTIVO);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoBeneficiario.ACTIVO);
    }

    @Test
    void crearBeneficiario_rechazaCorreoDuplicado() {
        final Beneficiario beneficiario = buildBeneficiario();
        final Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(99L);
        usuarioExistente.setCorreo(beneficiario.getCorreo());

        when(usuarioRepository.findByCorreo(beneficiario.getCorreo())).thenReturn(Optional.of(usuarioExistente));

        assertThatThrownBy(() -> beneficiarioService.crearBeneficiario(beneficiario))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(beneficiario.getCorreo());
    }

    @Test
    void actualizarBeneficiario_rechazaCorreoDuplicado() {
        final Beneficiario actual = buildBeneficiario();
        actual.setId(10L);
        actual.setCorreo("actual@example.com");

        final Beneficiario actualizado = buildBeneficiario();
        actualizado.setCorreo("duplicado@example.com");

        final Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(20L);
        usuarioExistente.setCorreo("duplicado@example.com");

        when(beneficiarioRepository.findById(10L)).thenReturn(Optional.of(actual));
        when(usuarioRepository.findByCorreo("duplicado@example.com")).thenReturn(Optional.of(usuarioExistente));

        assertThatThrownBy(() -> beneficiarioService.actualizarBeneficiario(10L, actualizado))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("duplicado@example.com");
    }

    @Test
    void obtenerPorId_lanzaExcepcionEspecificaSiNoExiste() {
        when(beneficiarioRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> beneficiarioService.obtenerPorId(5L))
                .isInstanceOf(BeneficiarioNotFoundException.class)
                .hasMessageContaining("5");
    }

    @Test
    void listar_usaFiltroCombinadoSinCargarTodoEnMemoria() {
        final List<Beneficiario> expected = List.of(buildBeneficiario());
        when(beneficiarioRepository.findByBarrioAndEstado(Barrio.CENTRO, EstadoBeneficiario.ACTIVO))
                .thenReturn(expected);

        final List<Beneficiario> resultado = beneficiarioService.listar(Barrio.CENTRO, EstadoBeneficiario.ACTIVO);

        assertThat(resultado).containsExactlyElementsOf(expected);
        verify(beneficiarioRepository).findByBarrioAndEstado(Barrio.CENTRO, EstadoBeneficiario.ACTIVO);
        verify(beneficiarioRepository, never()).findAll();
    }

    private Beneficiario buildBeneficiario() {
        final Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(1L);
        beneficiario.setNombre("Juan Perez");
        beneficiario.setCorreo("juan@example.com");
        beneficiario.setDireccion("Av. 18 de Julio 1234");
        beneficiario.setFechaNacimiento(LocalDate.of(1990, 1, 15));
        beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        beneficiario.setBarrio(Barrio.CENTRO);
        return beneficiario;
    }
}
