package edu.udelar.ayudemos.distribucion.infrastructure;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {

    @Override
    @EntityGraph(attributePaths = {"beneficiario", "donaciones", "repartidor"})
    List<Distribucion> findAll();

    @Override
    @EntityGraph(attributePaths = {"beneficiario", "donaciones", "repartidor"})
    Optional<Distribucion> findById(Long id);

    @EntityGraph(attributePaths = {"beneficiario", "donaciones", "repartidor"})
    List<Distribucion> findByEstado(EstadoDistribucion estado);

    @EntityGraph(attributePaths = {"beneficiario", "donaciones", "repartidor"})
    List<Distribucion> findByBeneficiarioBarrio(Barrio barrio);

    @EntityGraph(attributePaths = {"beneficiario", "donaciones", "repartidor"})
    List<Distribucion> findByBeneficiarioBarrioAndEstado(Barrio barrio, EstadoDistribucion estado);

    @EntityGraph(attributePaths = {"beneficiario", "donaciones", "repartidor"})
    @Query("SELECT d FROM Distribucion d WHERE d.fechaEntrega BETWEEN :fechaInicio AND :fechaFin")
    List<Distribucion> findByRangoFechas(@Param("fechaInicio") LocalDate fechaInicio,
                                         @Param("fechaFin") LocalDate fechaFin);
}
