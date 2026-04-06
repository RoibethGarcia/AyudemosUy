package edu.udelar.ayudemos.repartidor.infrastructure;

import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    Optional<Repartidor> findByNumeroLicencia(String numeroLicencia);
}
