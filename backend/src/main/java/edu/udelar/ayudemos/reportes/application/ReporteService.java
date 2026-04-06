package edu.udelar.ayudemos.reportes.application;

import edu.udelar.ayudemos.distribucion.infrastructure.DistribucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final DistribucionRepository distribucionRepository;

    @Transactional(readOnly = true)
    public List<ZonaDistribucionReporte> obtenerZonasConMayorNumeroDeDistribuciones() {
        return distribucionRepository.obtenerCantidadDistribucionesPorBarrio();
    }
}
