package edu.udelar.ayudemos.donacion.api.mapper;

import edu.udelar.ayudemos.donacion.api.dto.DonacionCreateRequest;
import edu.udelar.ayudemos.donacion.api.dto.DonacionResponse;
import edu.udelar.ayudemos.donacion.application.command.DonacionCreateCommand;
import edu.udelar.ayudemos.donacion.domain.Alimento;
import edu.udelar.ayudemos.donacion.domain.Articulo;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.donacion.domain.TipoDonacion;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DonacionMapperTest {

    private final DonacionMapper donacionMapper = Mappers.getMapper(DonacionMapper.class);

    @Test
    void toCreateCommand_mapeaElPayloadCompleto() {
        final DonacionCreateRequest request = new DonacionCreateRequest(
                TipoDonacion.ARTICULO,
                "DON-020",
                LocalDate.of(2026, 4, 2),
                "Mesa plegable",
                null,
                12.5,
                "120x60x75"
        );

        final DonacionCreateCommand command = donacionMapper.toCreateCommand(request);

        assertThat(command.tipo()).isEqualTo(TipoDonacion.ARTICULO);
        assertThat(command.numeroIdentificacion()).isEqualTo("DON-020");
        assertThat(command.fechaIngreso()).isEqualTo(LocalDate.of(2026, 4, 2));
        assertThat(command.descripcion()).isEqualTo("Mesa plegable");
        assertThat(command.peso()).isEqualTo(12.5);
        assertThat(command.dimensiones()).isEqualTo("120x60x75");
    }

    @Test
    void toResponse_mapeaAlimentoConCamposEspecificos() {
        final Donacion donacion = buildAlimento();

        final DonacionResponse response = donacionMapper.toResponse(donacion);

        assertThat(response.tipo()).isEqualTo(TipoDonacion.ALIMENTO);
        assertThat(response.descripcion()).isEqualTo("Arroz");
        assertThat(response.cantidad()).isEqualTo(15);
        assertThat(response.peso()).isNull();
        assertThat(response.dimensiones()).isNull();
    }

    @Test
    void toResponse_mapeaArticuloConCamposEspecificos() {
        final Donacion donacion = buildArticulo();

        final DonacionResponse response = donacionMapper.toResponse(donacion);

        assertThat(response.tipo()).isEqualTo(TipoDonacion.ARTICULO);
        assertThat(response.descripcion()).isEqualTo("Mesa plegable");
        assertThat(response.cantidad()).isNull();
        assertThat(response.peso()).isEqualTo(12.5);
        assertThat(response.dimensiones()).isEqualTo("120x60x75");
    }

    private Donacion buildAlimento() {
        final Alimento alimento = new Alimento();
        alimento.setId(1L);
        alimento.setNumeroIdentificacion("DON-001");
        alimento.setFechaIngreso(LocalDate.of(2026, 4, 1));
        alimento.setDescripcion("Arroz");
        alimento.setCantidad(15);
        return alimento;
    }

    private Donacion buildArticulo() {
        final Articulo articulo = new Articulo();
        articulo.setId(2L);
        articulo.setNumeroIdentificacion("DON-002");
        articulo.setFechaIngreso(LocalDate.of(2026, 4, 2));
        articulo.setDescripcion("Mesa plegable");
        articulo.setPeso(12.5);
        articulo.setDimensiones("120x60x75");
        return articulo;
    }
}
