package edu.udelar.ayudemos.repartidor.api;

import edu.udelar.ayudemos.repartidor.api.dto.RepartidorCreateRequest;
import edu.udelar.ayudemos.repartidor.api.dto.RepartidorResponse;
import edu.udelar.ayudemos.repartidor.api.dto.RepartidorUpdateRequest;
import edu.udelar.ayudemos.repartidor.api.mapper.RepartidorMapper;
import edu.udelar.ayudemos.repartidor.application.RepartidorService;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
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
@RequestMapping("/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    private final RepartidorService repartidorService;
    private final RepartidorMapper repartidorMapper;

    @PostMapping
    public ResponseEntity<RepartidorResponse> crearRepartidor(@Valid @RequestBody final RepartidorCreateRequest request) {
        final Repartidor repartidor = repartidorService.crearRepartidor(repartidorMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(repartidorMapper.toResponse(repartidor));
    }

    @GetMapping
    public ResponseEntity<List<RepartidorResponse>> listarRepartidores() {
        final List<RepartidorResponse> repartidores = repartidorService.listarRepartidores().stream()
                .map(repartidorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponse> obtenerRepartidor(@PathVariable final Long id) {
        final Repartidor repartidor = repartidorService.obtenerPorId(id);
        return ResponseEntity.ok(repartidorMapper.toResponse(repartidor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponse> actualizarRepartidor(
            @PathVariable final Long id,
            @Valid @RequestBody final RepartidorUpdateRequest request
    ) {
        final Repartidor repartidor = repartidorService.actualizarRepartidor(id, repartidorMapper.toEntity(request));
        return ResponseEntity.ok(repartidorMapper.toResponse(repartidor));
    }
}
