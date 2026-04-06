package edu.udelar.ayudemos.donacion.api;

import edu.udelar.ayudemos.donacion.api.dto.DonacionCreateRequest;
import edu.udelar.ayudemos.donacion.api.dto.DonacionResponse;
import edu.udelar.ayudemos.donacion.api.dto.DonacionUpdateRequest;
import edu.udelar.ayudemos.donacion.api.mapper.DonacionMapper;
import edu.udelar.ayudemos.donacion.application.DonacionService;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/donaciones")
@RequiredArgsConstructor
public class DonacionController {

    private final DonacionService donacionService;
    private final DonacionMapper donacionMapper;

    @PostMapping
    public ResponseEntity<DonacionResponse> crearDonacion(@Valid @RequestBody final DonacionCreateRequest request) {
        final Donacion donacion = donacionService.crearDonacion(donacionMapper.toCreateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(donacionMapper.toResponse(donacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonacionResponse> actualizarDonacion(
            @PathVariable final Long id,
            @Valid @RequestBody final DonacionUpdateRequest request
    ) {
        final Donacion donacion = donacionService.actualizarDonacion(id, donacionMapper.toUpdateCommand(request));
        return ResponseEntity.ok(donacionMapper.toResponse(donacion));
    }
}
