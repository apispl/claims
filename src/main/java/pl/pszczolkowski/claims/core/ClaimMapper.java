package pl.pszczolkowski.claims.core;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.entities.Claim;

@Mapper
interface ClaimMapper {

    Claim dtoToClaim(ClaimDTO claimDto);
    ClaimDTO claimToDto(Claim claim);
    Claim reqToClaim(ClaimRequest claimRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClaimFromDto(ClaimDTO dto, @MappingTarget Claim entity);
}
