package edu.udelar.ayudemos.service;

import edu.udelar.ayudemos.domain.Barrio;
import edu.udelar.ayudemos.domain.Beneficiario;
import edu.udelar.ayudemos.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.repository.BeneficiarioRepository;
import edu.udelar.ayudemos.repository.UsuarioRepository;
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
    public Beneficiario crearBeneficiario(Beneficiario beneficiario) {
        // Validar que el correo sea único
        if (usuarioRepository.existsByCorreo(beneficiario.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        
        // Si no tiene estado, asignar ACTIVO por defecto
        if (beneficiario.getEstado() == null) {
            beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        }
        
        return beneficiarioRepository.save(beneficiario);
    }
    
    public List<Beneficiario> listarTodos() {
        return beneficiarioRepository.findAll();
    }
    
    public List<Beneficiario> listarPorBarrio(Barrio barrio) {
        return beneficiarioRepository.findByBarrio(barrio);
    }
    
    public List<Beneficiario> listarPorEstado(EstadoBeneficiario estado) {
        return beneficiarioRepository.findByEstado(estado);
    }
    
    public Beneficiario obtenerPorId(Long id) {
        return beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiario no encontrado con ID: " + id));
    }
    
    @Transactional
    public Beneficiario actualizarBeneficiario(Long id, Beneficiario beneficiarioActualizado) {
        Beneficiario beneficiario = obtenerPorId(id);
        
        // Validar correo único si cambió
        if (!beneficiario.getCorreo().equals(beneficiarioActualizado.getCorreo()) &&
            usuarioRepository.existsByCorreo(beneficiarioActualizado.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        
        beneficiario.setNombre(beneficiarioActualizado.getNombre());
        beneficiario.setCorreo(beneficiarioActualizado.getCorreo());
        beneficiario.setDireccion(beneficiarioActualizado.getDireccion());
        beneficiario.setFechaNacimiento(beneficiarioActualizado.getFechaNacimiento());
        beneficiario.setEstado(beneficiarioActualizado.getEstado());
        beneficiario.setBarrio(beneficiarioActualizado.getBarrio());
        
        return beneficiarioRepository.save(beneficiario);
    }
}