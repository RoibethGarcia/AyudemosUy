package edu.udelar.ayudemos.usuario.application;

import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.usuario.application.exception.UsuarioNotFoundException;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crearUsuario(final Usuario usuario) {
        validarCorreoUnico(usuario.getCorreo(), null);
        usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash()));
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findBaseUsuarios();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(final Long id) {
        return usuarioRepository.findBaseUsuarioById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    @Transactional
    public Usuario actualizarUsuario(final Long id, final Usuario usuarioActualizado) {
        final Usuario usuario = obtenerPorId(id);

        validarCorreoUnico(usuarioActualizado.getCorreo(), usuario.getId());

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setCorreo(usuarioActualizado.getCorreo());

        return usuarioRepository.save(usuario);
    }

    private void validarCorreoUnico(final String correo, final Long usuarioPermitidoId) {
        usuarioRepository.findByCorreo(correo)
                .filter(usuario -> usuarioPermitidoId == null || !usuario.getId().equals(usuarioPermitidoId))
                .ifPresent(usuario -> {
                    throw new EmailAlreadyExistsException(correo);
                });
    }
}
