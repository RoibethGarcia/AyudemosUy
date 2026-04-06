package edu.udelar.ayudemos.reportes.api;

import edu.udelar.ayudemos.reportes.api.dto.ZonaMayorDistribucionResponse;
import edu.udelar.ayudemos.reportes.api.mapper.ReporteMapper;
import edu.udelar.ayudemos.reportes.application.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteMapper reporteMapper;

    @GetMapping("/zonas-mayor-distribuciones")
    public ResponseEntity<List<ZonaMayorDistribucionResponse>> obtenerZonasConMayorNumeroDeDistribuciones() {
        final List<ZonaMayorDistribucionResponse> response = reporteService.obtenerZonasConMayorNumeroDeDistribuciones()
                .stream()
                .map(reporteMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
