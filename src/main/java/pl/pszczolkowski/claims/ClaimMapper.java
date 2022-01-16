package pl.pszczolkowski.claims;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.pszczolkowski.claims.entities.Claim;

@Mapper
public interface ClaimMapper {

    Claim dtoToClaim(ClaimDTO claimDto);
    ClaimDTO claimToDto(Claim claim);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClaimFromDto(ClaimDTO dto, @MappingTarget Claim entity);
}
