package pl.pszczolkowski.claims.core;

import org.springframework.stereotype.Service;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.entities.Claim;
import pl.pszczolkowski.claims.core.entities.StatusE;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;

@Service
public class ClaimProcessor {

    private final ClaimRepository claimRepository;

    public ClaimProcessor(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public ClaimDTO process(String claimIdentifier, ActionE actionE) throws ClaimNotFoundException, ClaimProcessingException {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimIdentifier)
                .orElseThrow(ClaimNotFoundException::new);

        StatusE status = claim.getStatus();

        if (actionE.isPositive)
            status = status.positiveProcess();
        else
            status = status.negativeProcess();

        claim.setStatus(status);
        claimRepository.save(claim);

        return null;

    }
}
