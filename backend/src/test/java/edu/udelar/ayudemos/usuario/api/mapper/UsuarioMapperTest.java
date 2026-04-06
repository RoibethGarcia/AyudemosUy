package edu.udelar.ayudemos.usuario.api.mapper;

import edu.udelar.ayudemos.usuario.api.dto.UsuarioCreateRequest;
import edu.udelar.ayudemos.usuario.api.dto.UsuarioResponse;
import edu.udelar.ayudemos.usuario.api.dto.UsuarioUpdateRequest;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest {

    private final UsuarioMapper usuarioMapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    void toEntity_desdeCreateRequest_mapeaCamposEsperados() {
        final UsuarioCreateRequest request = new UsuarioCreateRequest("Juan Perez", "juan@example.com");

        final Usuario usuario = usuarioMapper.toEntity(request);

        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getNombre()).isEqualTo("Juan Perez");
        assertThat(usuario.getCorreo()).isEqualTo("juan@example.com");
    }

    @Test
    void toEntity_desdeUpdateRequest_mapeaCamposEsperados() {
        final UsuarioUpdateRequest request = new UsuarioUpdateRequest("Ana Gomez", "ana@example.com");

        final Usuario usuario = usuarioMapper.toEntity(request);

        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getNombre()).isEqualTo("Ana Gomez");
        assertThat(usuario.getCorreo()).isEqualTo("ana@example.com");
    }

    @Test
    void toResponse_mapeaCamposEsperados() {
        final Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Maria Lopez");
        usuario.setCorreo("maria@example.com");

        final UsuarioResponse response = usuarioMapper.toResponse(usuario);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.nombre()).isEqualTo("Maria Lopez");
        assertThat(response.correo()).isEqualTo("maria@example.com");
    }
}