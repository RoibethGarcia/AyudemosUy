package edu.udelar.ayudemos.auth.application;

import edu.udelar.ayudemos.auth.api.dto.AuthResponse;
import edu.udelar.ayudemos.auth.api.dto.LoginRequest;
import edu.udelar.ayudemos.auth.application.exception.InvalidCredentialsException;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthResponse autenticar(final LoginRequest request) {
        final Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(InvalidCredentialsException::new);

        final boolean credencialesValidas = usuario.getContrasenaHash() != null
                && passwordEncoder.matches(request.contrasena(), usuario.getContrasenaHash());

        if (!credencialesValidas) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                resolverTipoUsuario(usuario)
        );
    }

    private String resolverTipoUsuario(final Usuario usuario) {
        if (usuario instanceof Beneficiario) {
            return "BENEFICIARIO";
        }
        if (usuario instanceof Repartidor) {
            return "REPARTIDOR";
        }
        return "USUARIO";
    }
}
