package edu.udelar.ayudemos.repository;

import edu.udelar.ayudemos.domain.Barrio;
import edu.udelar.ayudemos.domain.Beneficiario;
import edu.udelar.ayudemos.domain.EstadoBeneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
    List<Beneficiario> findByBarrio(Barrio barrio);
    List<Beneficiario> findByEstado(EstadoBeneficiario estado);
    List<Beneficiario> findByBarrioAndEstado(Barrio barrio, EstadoBeneficiario estado);
}