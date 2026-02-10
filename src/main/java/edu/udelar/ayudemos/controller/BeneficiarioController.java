package edu.udelar.ayudemos.controller;

import edu.udelar.ayudemos.domain.Barrio;
import edu.udelar.ayudemos.domain.Beneficiario;
import edu.udelar.ayudemos.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.service.BeneficiarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beneficiarios")
@RequiredArgsConstructor
public class BeneficiarioController {
    
    private final BeneficiarioService beneficiarioService;
    
    @PostMapping
    public ResponseEntity<Beneficiario> crearBeneficiario(@Valid @RequestBody Beneficiario beneficiario) {
        Beneficiario nuevoBeneficiario = beneficiarioService.crearBeneficiario(beneficiario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBeneficiario);
    }
    
    @GetMapping
    public ResponseEntity<List<Beneficiario>> listarBeneficiarios(
            @RequestParam(required = false) Barrio zona,
            @RequestParam(required = false) EstadoBeneficiario estado) {
        
        List<Beneficiario> beneficiarios;
        
        if (zona != null && estado != null) {
            beneficiarios = beneficiarioService.listarTodos().stream()
                    .filter(b -> b.getBarrio() == zona && b.getEstado() == estado)
                    .toList();
        } else if (zona != null) {
            beneficiarios = beneficiarioService.listarPorBarrio(zona);
        } else if (estado != null) {
            beneficiarios = beneficiarioService.listarPorEstado(estado);
        } else {
            beneficiarios = beneficiarioService.listarTodos();
        }
        
        return ResponseEntity.ok(beneficiarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Beneficiario> obtenerBeneficiario(@PathVariable Long id) {
        Beneficiario beneficiario = beneficiarioService.obtenerPorId(id);
        return ResponseEntity.ok(beneficiario);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Beneficiario> actualizarBeneficiario(
            @PathVariable Long id,
            @Valid @RequestBody Beneficiario beneficiario) {
        Beneficiario actualizado = beneficiarioService.actualizarBeneficiario(id, beneficiario);
        return ResponseEntity.ok(actualizado);
    }
}