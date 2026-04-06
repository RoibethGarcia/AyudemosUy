package edu.udelar.ayudemos.repartidor.api.mapper;

import edu.udelar.ayudemos.repartidor.api.dto.RepartidorCreateRequest;
import edu.udelar.ayudemos.repartidor.api.dto.RepartidorResponse;
import edu.udelar.ayudemos.repartidor.api.dto.RepartidorUpdateRequest;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RepartidorMapperTest {

    private final RepartidorMapper repartidorMapper = Mappers.getMapper(RepartidorMapper.class);

    @Test
    void toEntity_desdeCreateRequest_mapeaCamposEsperados() {
        final RepartidorCreateRequest request = new RepartidorCreateRequest(
                "Juan Perez",
                "juan@example.com",
                "repartidor123",
                "LIC-001"
        );

        final Repartidor repartidor = repartidorMapper.toEntity(request);

        assertThat(repartidor.getId()).isNull();
        assertThat(repartidor.getNombre()).isEqualTo("Juan Perez");
        assertThat(repartidor.getCorreo()).isEqualTo("juan@example.com");
        assertThat(repartidor.getContrasenaHash()).isEqualTo("repartidor123");
        assertThat(repartidor.getNumeroLicencia()).isEqualTo("LIC-001");
    }

    @Test
    void toEntity_desdeUpdateRequest_mapeaCamposEsperados() {
        final RepartidorUpdateRequest request = new RepartidorUpdateRequest(
                "Ana Gomez",
                "ana@example.com",
                "LIC-002"
        );

        final Repartidor repartidor = repartidorMapper.toEntity(request);

        assertThat(repartidor.getId()).isNull();
        assertThat(repartidor.getNombre()).isEqualTo("Ana Gomez");
        assertThat(repartidor.getCorreo()).isEqualTo("ana@example.com");
        assertThat(repartidor.getNumeroLicencia()).isEqualTo("LIC-002");
    }

    @Test
    void toResponse_mapeaCamposEsperados() {
        final Repartidor repartidor = new Repartidor();
        repartidor.setId(7L);
        repartidor.setNombre("Mario Gomez");
        repartidor.setCorreo("mario@example.com");
        repartidor.setNumeroLicencia("LIC-007");

        final RepartidorResponse response = repartidorMapper.toResponse(repartidor);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.nombre()).isEqualTo("Mario Gomez");
        assertThat(response.correo()).isEqualTo("mario@example.com");
        assertThat(response.numeroLicencia()).isEqualTo("LIC-007");
    }
}
