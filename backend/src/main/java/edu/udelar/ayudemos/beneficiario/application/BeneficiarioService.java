package edu.udelar.ayudemos.beneficiario.application;

import edu.udelar.ayudemos.beneficiario.application.exception.BeneficiarioNotFoundException;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.beneficiario.infrastructure.BeneficiarioRepository;
import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Beneficiario crearBeneficiario(final Beneficiario beneficiario) {
        validarCorreoUnico(beneficiario.getCorreo(), null);

        if (beneficiario.getEstado() == null) {
            beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        }

        return beneficiarioRepository.save(beneficiario);
    }

    @Transactional(readOnly = true)
    public List<Beneficiario> listar(final Barrio barrio, final EstadoBeneficiario estado) {
        if (barrio != null && estado != null) {
            return beneficiarioRepository.findByBarrioAndEstado(barrio, estado);
        }
        if (barrio != null) {
            return beneficiarioRepository.findByBarrio(barrio);
        }
        if (estado != null) {
            return beneficiarioRepository.findByEstado(estado);
        }
        return beneficiarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Beneficiario obtenerPorId(final Long id) {
        return beneficiarioRepository.findById(id)
                .orElseThrow(() -> new BeneficiarioNotFoundException(id));
    }

    @Transactional
    public Beneficiario actualizarBeneficiario(final Long id, final Beneficiario beneficiarioActualizado) {
        final Beneficiario beneficiario = obtenerPorId(id);

        validarCorreoUnico(beneficiarioActualizado.getCorreo(), beneficiario.getId());

        beneficiario.setNombre(beneficiarioActualizado.getNombre());
        beneficiario.setCorreo(beneficiarioActualizado.getCorreo());
        beneficiario.setDireccion(beneficiarioActualizado.getDireccion());
        beneficiario.setFechaNacimiento(beneficiarioActualizado.getFechaNacimiento());
        beneficiario.setEstado(beneficiarioActualizado.getEstado());
        beneficiario.setBarrio(beneficiarioActualizado.getBarrio());

        return beneficiarioRepository.save(beneficiario);
    }

    private void validarCorreoUnico(final String correo, final Long usuarioPermitidoId) {
        usuarioRepository.findByCorreo(correo)
                .filter(usuario -> usuarioPermitidoId == null || !usuario.getId().equals(usuarioPermitidoId))
                .ifPresent(usuario -> {
                    throw new EmailAlreadyExistsException(correo);
                });
    }
}
