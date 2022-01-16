package pl.pszczolkowski.claims;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import pl.pszczolkowski.claims.entities.Claim;
import pl.pszczolkowski.claims.entities.StatusE;
import pl.pszczolkowski.claims.exceptions.ClaimNotFoundException;

@Service
class ClaimService {

    private final ClaimMapper claimMapper = Mappers.getMapper(ClaimMapper.class);
    private final ClaimRepository claimRepository;

    ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    ClaimDTO getClaimByName(String claimName) throws ClaimNotFoundException {

        Claim claim = claimRepository
                .findClaimByName(claimName)
                .orElseThrow(ClaimNotFoundException::new);

        return claimMapper.claimToDto(claim);
    }

    ClaimDTO saveClaim(ClaimRequest claimRequest) {
        //TODO validation for request

        Claim claimMapped = Claim.builder()
                .name(claimRequest.getName())
                .content(claimRequest.getContent())
                .status(StatusE.CREATED)
                .build();

        Claim claim = claimRepository.save(claimMapped);
        return claimMapper.claimToDto(claim);
    }

    ClaimDTO editClaim(ClaimDTO claimDTO) throws ClaimContentCannotBeChanged, ClaimNotFoundException {
        Claim claim = claimRepository
                .findById(claimDTO.getId())
                .orElseThrow(ClaimNotFoundException::new);

        //TODO validation only if claim status is CREATED, VERIFIED
        boolean isContentChanged = !claimDTO.getContent().equals(claim.getContent());
        if (isContentChanged
                && !StatusE.CREATED.equals(claimDTO.getStatus())
                && !StatusE.VERIFIED.equals(claimDTO.getStatus())) {
            throw new ClaimContentCannotBeChanged();
        }

        claimMapper.updateClaimFromDto(claimDTO, claim);
        Claim updatedClaim = claimRepository.save(claim);

        return claimMapper.claimToDto(updatedClaim);
    }
}
