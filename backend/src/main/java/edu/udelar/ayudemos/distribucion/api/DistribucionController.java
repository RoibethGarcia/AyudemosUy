package edu.udelar.ayudemos.distribucion.api;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.distribucion.api.dto.DistribucionCreateRequest;
import edu.udelar.ayudemos.distribucion.api.dto.DistribucionResponse;
import edu.udelar.ayudemos.distribucion.api.dto.DistribucionUpdateRequest;
import edu.udelar.ayudemos.distribucion.api.mapper.DistribucionMapper;
import edu.udelar.ayudemos.distribucion.application.DistribucionService;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/distribuciones")
@RequiredArgsConstructor
public class DistribucionController {

    private final DistribucionService distribucionService;
    private final DistribucionMapper distribucionMapper;

    @PostMapping
    public ResponseEntity<DistribucionResponse> crearDistribucion(
            @Valid @RequestBody final DistribucionCreateRequest request
    ) {
        final Distribucion distribucion = distribucionService.crearDistribucion(
                distribucionMapper.toCreateCommand(request)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(distribucionMapper.toResponse(distribucion));
    }

    @GetMapping
    public ResponseEntity<List<DistribucionResponse>> listarDistribuciones(
            @RequestParam(required = false) final Barrio zona,
            @RequestParam(required = false) final EstadoDistribucion estado
    ) {
        final List<DistribucionResponse> distribuciones = distribucionService.listar(zona, estado).stream()
                .map(distribucionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(distribuciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistribucionResponse> obtenerDistribucion(@PathVariable final Long id) {
        final Distribucion distribucion = distribucionService.obtenerPorId(id);
        return ResponseEntity.ok(distribucionMapper.toResponse(distribucion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistribucionResponse> actualizarDistribucion(
            @PathVariable final Long id,
            @Valid @RequestBody final DistribucionUpdateRequest request
    ) {
        final Distribucion actualizada = distribucionService.actualizarDistribucion(
                id,
                distribucionMapper.toUpdateCommand(request)
        );
        return ResponseEntity.ok(distribucionMapper.toResponse(actualizada));
    }
}
