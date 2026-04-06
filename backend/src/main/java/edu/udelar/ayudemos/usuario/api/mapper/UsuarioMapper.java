package edu.udelar.ayudemos.usuario.api.mapper;

import edu.udelar.ayudemos.usuario.api.dto.UsuarioCreateRequest;
import edu.udelar.ayudemos.usuario.api.dto.UsuarioResponse;
import edu.udelar.ayudemos.usuario.api.dto.UsuarioUpdateRequest;
import edu.udelar.ayudemos.usuario.domain.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contrasenaHash", source = "contrasena")
    Usuario toEntity(UsuarioCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contrasenaHash", ignore = true)
    Usuario toEntity(UsuarioUpdateRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
