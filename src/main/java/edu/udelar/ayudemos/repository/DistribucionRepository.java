package edu.udelar.ayudemos.repository;

import edu.udelar.ayudemos.domain.Barrio;
import edu.udelar.ayudemos.domain.Distribucion;
import edu.udelar.ayudemos.domain.EstadoDistribucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {
    List<Distribucion> findByEstado(EstadoDistribucion estado);
    
    @Query("SELECT d FROM Distribucion d WHERE d.beneficiario.barrio = :barrio")
    List<Distribucion> findByBarrio(@Param("barrio") Barrio barrio);
    
    @Query("SELECT d FROM Distribucion d WHERE d.fechaEntrega BETWEEN :fechaInicio AND :fechaFin")
    List<Distribucion> findByRangoFechas(@Param("fechaInicio") LocalDate fechaInicio, 
                                          @Param("fechaFin") LocalDate fechaFin);
}