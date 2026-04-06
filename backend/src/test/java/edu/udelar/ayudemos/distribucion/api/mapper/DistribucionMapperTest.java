package edu.udelar.ayudemos.distribucion.api.mapper;

import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import edu.udelar.ayudemos.distribucion.api.dto.DistribucionResponse;
import edu.udelar.ayudemos.distribucion.domain.Distribucion;
import edu.udelar.ayudemos.distribucion.domain.EstadoDistribucion;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DistribucionMapperTest {

    private final DistribucionMapper distribucionMapper = Mappers.getMapper(DistribucionMapper.class);

    @Test
    void toResponse_mapeaRelacionesEIdsAnidados() {
        final Distribucion distribucion = new Distribucion();
        distribucion.setId(20L);
        distribucion.setFechaPreparacion(LocalDate.of(2026, 4, 1));
        distribucion.setFechaEntrega(LocalDate.of(2026, 4, 2));
        distribucion.setEstado(EstadoDistribucion.ENTREGADO);
        distribucion.setBeneficiario(buildBeneficiario());
        distribucion.setDonaciones(List.of(buildDonacion(100L), buildDonacion(101L)));
        distribucion.setRepartidor(buildRepartidor());

        final DistribucionResponse response = distribucionMapper.toResponse(distribucion);

        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.beneficiarioId()).isEqualTo(8L);
        assertThat(response.beneficiarioNombre()).isEqualTo("Ana Perez");
        assertThat(response.barrio()).isEqualTo(Barrio.CENTRO);
        assertThat(response.donacionIds()).containsExactly(100L, 101L);
        assertThat(response.repartidorId()).isEqualTo(50L);
        assertThat(response.repartidorNombre()).isEqualTo("Mario Gomez");
    }

    private Beneficiario buildBeneficiario() {
        final Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(8L);
        beneficiario.setNombre("Ana Perez");
        beneficiario.setCorreo("ana@example.com");
        beneficiario.setDireccion("Av. Rivera 123");
        beneficiario.setFechaNacimiento(LocalDate.of(1992, 6, 10));
        beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        beneficiario.setBarrio(Barrio.CENTRO);
        return beneficiario;
    }

    private Donacion buildDonacion(final Long id) {
        final Donacion donacion = new Donacion();
        donacion.setId(id);
        donacion.setNumeroIdentificacion("DON-" + id);
        donacion.setFechaIngreso(LocalDate.of(2026, 3, 30));
        return donacion;
    }

    private Repartidor buildRepartidor() {
        final Repartidor repartidor = new Repartidor();
        repartidor.setId(50L);
        repartidor.setNombre("Mario Gomez");
        repartidor.setCorreo("mario@example.com");
        repartidor.setNumeroLicencia("LIC-50");
        return repartidor;
    }
}
