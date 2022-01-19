package pl.pszczolkowski.claims.core;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimProcessRequest;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.entities.Claim;
import pl.pszczolkowski.claims.core.entities.History;
import pl.pszczolkowski.claims.core.entities.SharingNumber;
import pl.pszczolkowski.claims.core.entities.StatusE;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;
import pl.pszczolkowski.claims.core.exceptions.ReasonForActionRequired;

import java.util.UUID;

@Service
public class ClaimProcessor {

    private final ClaimMapper claimMapper = Mappers.getMapper(ClaimMapper.class);
    private final ClaimRepository claimRepository;
    private final HistoryRepository historyRepository;

    public ClaimProcessor(ClaimRepository claimRepository,
                          HistoryRepository historyRepository) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
    }

    public ClaimDTO process(ClaimProcessRequest claimProcessRequest) throws ClaimNotFoundException, ClaimProcessingException, ReasonForActionRequired {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimProcessRequest.getClaimIdentifier())
                .orElseThrow(ClaimNotFoundException::new);

        StatusE status = claim.getStatus();
        if (claimProcessRequest.getAction().isPositive) {
            status = status.positiveProcess();
            historyRepository.save(History.builder()
                    .claim(claim)
                    .actionClaim(claimProcessRequest.getAction())
                    .build());
            if (ActionE.PUBLISH.equals(claimProcessRequest.getAction())){
                claim.setSharingNumber(SharingNumber.builder().uuid(UUID.randomUUID().toString()).build());
            }
        } else {
            if (claimProcessRequest.getOptionalReason() == null)
                throw new ReasonForActionRequired("Reason cannot be null.");

            status = status.negativeProcess();
            historyRepository.save(History.builder()
                    .claim(claim)
                    .actionClaim(claimProcessRequest.getAction())
                    .optionalReason(claimProcessRequest.getOptionalReason())
                    .build());
        }
        claim.setStatus(status);
        Claim updatedClaim = claimRepository.save(claim);

        return claimMapper.claimToDto(updatedClaim);

    }
}
