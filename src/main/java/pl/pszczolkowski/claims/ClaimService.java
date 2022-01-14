package pl.pszczolkowski.claims;

import org.springframework.stereotype.Service;

@Service
class ClaimService {

    private final ClaimRepository claimRepository;

    ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    Claim saveClaim(ClaimRequest claimRequest) {
        //TODO validation for request

        Claim claim = Claim.builder()
                .name(claimRequest.getName())
                .content(claimRequest.getContent())
                .status(Status.CREATED)
                .build();

        return claimRepository.save(claim);
    }
}
