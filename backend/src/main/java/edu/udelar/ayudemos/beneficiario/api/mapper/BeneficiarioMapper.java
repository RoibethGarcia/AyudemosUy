package edu.udelar.ayudemos.beneficiario.api.mapper;

import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioCreateRequest;
import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioResponse;
import edu.udelar.ayudemos.beneficiario.api.dto.BeneficiarioUpdateRequest;
import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BeneficiarioMapper {

    @Mapping(target = "id", ignore = true)
    Beneficiario toEntity(BeneficiarioCreateRequest request);

    @Mapping(target = "id", ignore = true)
    Beneficiario toEntity(BeneficiarioUpdateRequest request);

    BeneficiarioResponse toResponse(Beneficiario beneficiario);
}
