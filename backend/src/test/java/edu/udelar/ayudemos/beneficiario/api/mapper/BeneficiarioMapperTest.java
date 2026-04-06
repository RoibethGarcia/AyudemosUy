package edu.udelar.ayudemos.beneficiario.api.mapper;

import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioCreateRequest;
import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioResponse;
import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioUpdateRequest;
import edu.udelar.ayudemos.beneficiario.domain.Barrio;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.beneficiario.domain.EstadoBeneficiario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BeneficiarioMapperTest {

    private final BeneficiarioMapper beneficiarioMapper = Mappers.getMapper(BeneficiarioMapper.class);

    @Test
    void toEntity_desdeCreateRequest_mapeaCamposEsperados() {
        final BeneficiarioCreateRequest request = new BeneficiarioCreateRequest(
                "Juan Perez",
                "juan@example.com",
                "Av. 18 de Julio 1234",
                LocalDate.of(1990, 1, 15),
                EstadoBeneficiario.ACTIVO,
                Barrio.CENTRO
        );

        final Beneficiario beneficiario = beneficiarioMapper.toEntity(request);

        assertThat(beneficiario.getId()).isNull();
        assertThat(beneficiario.getNombre()).isEqualTo("Juan Perez");
        assertThat(beneficiario.getCorreo()).isEqualTo("juan@example.com");
        assertThat(beneficiario.getDireccion()).isEqualTo("Av. 18 de Julio 1234");
        assertThat(beneficiario.getFechaNacimiento()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(beneficiario.getEstado()).isEqualTo(EstadoBeneficiario.ACTIVO);
        assertThat(beneficiario.getBarrio()).isEqualTo(Barrio.CENTRO);
    }

    @Test
    void toEntity_desdeUpdateRequest_mapeaCamposEsperados() {
        final BeneficiarioUpdateRequest request = new BeneficiarioUpdateRequest(
                "Ana Gomez",
                "ana@example.com",
                "Colonia 987",
                LocalDate.of(1988, 6, 5),
                EstadoBeneficiario.SUSPENDIDO,
                Barrio.CORDON
        );

        final Beneficiario beneficiario = beneficiarioMapper.toEntity(request);

        assertThat(beneficiario.getId()).isNull();
        assertThat(beneficiario.getNombre()).isEqualTo("Ana Gomez");
        assertThat(beneficiario.getCorreo()).isEqualTo("ana@example.com");
        assertThat(beneficiario.getDireccion()).isEqualTo("Colonia 987");
        assertThat(beneficiario.getFechaNacimiento()).isEqualTo(LocalDate.of(1988, 6, 5));
        assertThat(beneficiario.getEstado()).isEqualTo(EstadoBeneficiario.SUSPENDIDO);
        assertThat(beneficiario.getBarrio()).isEqualTo(Barrio.CORDON);
    }

    @Test
    void toResponse_mapeaCamposEsperados() {
        final Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(7L);
        beneficiario.setNombre("Maria Lopez");
        beneficiario.setCorreo("maria@example.com");
        beneficiario.setDireccion("Maldonado 456");
        beneficiario.setFechaNacimiento(LocalDate.of(1995, 3, 21));
        beneficiario.setEstado(EstadoBeneficiario.ACTIVO);
        beneficiario.setBarrio(Barrio.PALERMO);

        final BeneficiarioResponse response = beneficiarioMapper.toResponse(beneficiario);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.nombre()).isEqualTo("Maria Lopez");
        assertThat(response.correo()).isEqualTo("maria@example.com");
        assertThat(response.direccion()).isEqualTo("Maldonado 456");
        assertThat(response.fechaNacimiento()).isEqualTo(LocalDate.of(1995, 3, 21));
        assertThat(response.estado()).isEqualTo(EstadoBeneficiario.ACTIVO);
        assertThat(response.barrio()).isEqualTo(Barrio.PALERMO);
    }
}
