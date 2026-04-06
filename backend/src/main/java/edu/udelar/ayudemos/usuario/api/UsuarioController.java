package edu.udelar.ayudemos.usuario.api;

import edu.udelar.ayudemos.usuario.api.dto.UsuarioCreateRequest;
import edu.udelar.ayudemos.usuario.api.dto.UsuarioResponse;
import edu.udelar.ayudemos.usuario.api.dto.UsuarioUpdateRequest;
import edu.udelar.ayudemos.usuario.api.mapper.UsuarioMapper;
import edu.udelar.ayudemos.usuario.application.UsuarioService;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody final UsuarioCreateRequest request) {
        final Usuario usuario = usuarioService.crearUsuario(usuarioMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toResponse(usuario));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        final List<UsuarioResponse> usuarios = usuarioService.listarUsuarios().stream()
                .map(usuarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable final Long id) {
        final Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuarioMapper.toResponse(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable final Long id,
            @Valid @RequestBody final UsuarioUpdateRequest request
    ) {
        final Usuario usuario = usuarioService.actualizarUsuario(id, usuarioMapper.toEntity(request));
        return ResponseEntity.ok(usuarioMapper.toResponse(usuario));
    }
}