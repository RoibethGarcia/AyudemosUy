package edu.udelar.ayudemos.beneficiario.api;

import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioCreateRequest;
import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioResponse;
import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioUpdateRequest;
import edu.udelar.ayudemos.beneficiario.api.mapper.BeneficiarioMapper;
import edu.udelar.ayudemos.beneficiario.application.BeneficiarioService;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
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
@RequestMapping("/beneficiarios")
@RequiredArgsConstructor
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;
    private final BeneficiarioMapper beneficiarioMapper;

    @PostMapping
    public ResponseEntity<BeneficiarioResponse> crearBeneficiario(
            @Valid @RequestBody final BeneficiarioCreateRequest request) {
        final Beneficiario nuevoBeneficiario = beneficiarioService
                .crearBeneficiario(beneficiarioMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(beneficiarioMapper.toResponse(nuevoBeneficiario));
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioResponse>> listarBeneficiarios(
            @RequestParam(required = false) final Barrio zona,
            @RequestParam(required = false) final EstadoBeneficiario estado) {
        final List<BeneficiarioResponse> beneficiarios = beneficiarioService.listar(zona, estado).stream()
                .map(beneficiarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(beneficiarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiarioResponse> obtenerBeneficiario(@PathVariable final Long id) {
        final Beneficiario beneficiario = beneficiarioService.obtenerPorId(id);
        return ResponseEntity.ok(beneficiarioMapper.toResponse(beneficiario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiarioResponse> actualizarBeneficiario(
            @PathVariable final Long id,
            @Valid @RequestBody final BeneficiarioUpdateRequest request) {
        final Beneficiario actualizado = beneficiarioService.actualizarBeneficiario(id,
                beneficiarioMapper.toEntity(request));
        return ResponseEntity.ok(beneficiarioMapper.toResponse(actualizado));
    }
}
