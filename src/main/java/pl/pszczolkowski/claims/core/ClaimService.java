package pl.pszczolkowski.claims.core;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.entities.Claim;
import pl.pszczolkowski.claims.core.entities.History;
import pl.pszczolkowski.claims.core.entities.StatusE;
import pl.pszczolkowski.claims.core.exceptions.ClaimContentCannotBeChangedException;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;

import javax.transaction.Transactional;

@Service
@Transactional
public class ClaimService {

    private final ClaimMapper claimMapper = Mappers.getMapper(ClaimMapper.class);
    private final ClaimRepository claimRepository;
    private final HistoryRepository historyRepository;

    public ClaimService(ClaimRepository claimRepository,
                        HistoryRepository historyRepository) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
    }

    public ClaimDTO getClaimByIdentifier(String claimIdentifier) throws ClaimNotFoundException {

        Claim claim = claimRepository
                .findClaimByIdentifier(claimIdentifier)
                .orElseThrow(ClaimNotFoundException::new);

        return claimMapper.claimToDto(claim);
    }

    public ClaimDTO saveClaim(ClaimRequest claimRequest) {
        //TODO validation for request

        Claim savedClaim = Claim.builder()
                .identifier(claimRequest.getIdentifier())
                .name(claimRequest.getName())
                .content(claimRequest.getContent())
                .status(StatusE.CREATED)
                .build();

        Claim claim = claimRepository.save(savedClaim);

        historyRepository.save(History.builder()
                .claim(savedClaim)
                .actionClaim(ActionE.CREATE).build());

        return claimMapper.claimToDto(claim);
    }

    public ClaimDTO editClaim(String claimIdentifier, String claimContent) throws ClaimContentCannotBeChangedException, ClaimNotFoundException {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimIdentifier)
                .orElseThrow(ClaimNotFoundException::new);

        //TODO validation only if claim status is CREATED, VERIFIED
        boolean isContentChanged = !claimContent.equals(claim.getContent());
        if (isContentChanged
                && !StatusE.CREATED.equals(claim.getStatus())
                && !StatusE.VERIFIED.equals(claim.getStatus())) {
            throw new ClaimContentCannotBeChangedException();
        }

        claimMapper.updateClaimFromDto(ClaimDTO.builder().content(claimContent).build(), claim);
        Claim updatedClaim = claimRepository.save(claim);

        historyRepository.save(History.builder()
                .claim(updatedClaim)
                .actionClaim(ActionE.EDIT).build());

        return claimMapper.claimToDto(updatedClaim);
    }
}
