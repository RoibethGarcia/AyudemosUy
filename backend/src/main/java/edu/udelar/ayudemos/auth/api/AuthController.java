package edu.udelar.ayudemos.auth.api;

import edu.udelar.ayudemos.auth.api.dto.AuthResponse;
import edu.udelar.ayudemos.auth.api.dto.LoginRequest;
import edu.udelar.ayudemos.auth.application.AuthService;
import edu.udelar.ayudemos.auth.application.exception.UnauthenticatedSessionException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    static final String AUTH_SESSION_ATTRIBUTE = "AUTH_SESSION";

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody final LoginRequest request,
            final HttpSession session
    ) {
        final AuthResponse authResponse = authService.autenticar(request);
        session.setAttribute(AUTH_SESSION_ATTRIBUTE, authResponse);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/sesion")
    public ResponseEntity<AuthResponse> obtenerSesion(final HttpSession session) {
        final Object authSession = session.getAttribute(AUTH_SESSION_ATTRIBUTE);
        if (!(authSession instanceof AuthResponse authResponse)) {
            throw new UnauthenticatedSessionException();
        }
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
