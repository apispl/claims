package pl.pszczolkowski.claims.claimcore;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.pszczolkowski.claims.claimcore.dto.ClaimDTO;
import pl.pszczolkowski.claims.claimcore.dto.ClaimProcessRequest;
import pl.pszczolkowski.claims.claimcore.dto.ClaimRequest;
import pl.pszczolkowski.claims.claimcore.dto.ClaimRequestParams;
import pl.pszczolkowski.claims.claimcore.enums.ActionE;
import pl.pszczolkowski.claims.claimcore.entities.Claim;
import pl.pszczolkowski.claims.claimcore.entities.SharingNumber;
import pl.pszczolkowski.claims.claimcore.statemachine.StatusE;
import pl.pszczolkowski.claims.claimcore.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.claimcore.exceptions.ClaimProcessingException;
import pl.pszczolkowski.claims.utils.ValidatorClaim;

import java.util.UUID;

public class ClaimFacade {

    private final ClaimMapper claimMapper = Mappers.getMapper(ClaimMapper.class);
    private final ClaimRepository claimRepository;
    private final HistoryService historyService;

    public ClaimFacade(ClaimRepository claimRepository, HistoryService historyService) {
        this.claimRepository = claimRepository;
        this.historyService = historyService;
    }

    public ClaimDTO getClaimByIdentifier(String claimIdentifier) throws ClaimNotFoundException {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimIdentifier)
                .orElseThrow(ClaimNotFoundException::new);

        return claimMapper.claimToDto(claim);
    }

    public Page<ClaimDTO> getClaims(ClaimRequestParams params, Pageable page) {
        return claimRepository.findAll(
                        ClaimSpecification.claimIdentifierEquals(params.getIdentifier())
                                .and(ClaimSpecification.claimStatusEquals(params.getStatus())), page)
                .map(claimMapper::claimToDto);
    }

    public ClaimDTO saveClaim(ClaimRequest claimRequest) {
        Claim savedClaim = claimMapper.reqToClaim(claimRequest);
        savedClaim.setStatus(StatusE.CREATED);
        Claim claim = claimRepository.save(savedClaim);

        historyService.archive(ActionE.CREATE, savedClaim);

        return claimMapper.claimToDto(claim);
    }

    public ClaimDTO editClaim(String claimIdentifier, String claimContent) throws ClaimNotFoundException, ClaimProcessingException {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimIdentifier)
                .orElseThrow(ClaimNotFoundException::new);

        //TODO validation only if claim status is CREATED, VERIFIED
        ValidatorClaim.checkIsActionAllowed(ActionE.EDIT, claim.getStatus());

        claimMapper.updateClaimFromDto(ClaimDTO.builder().content(claimContent).build(), claim);
        Claim updatedClaim = claimRepository.save(claim);

        historyService.archive(ActionE.EDIT, updatedClaim);
        return claimMapper.claimToDto(updatedClaim);
    }

    public ClaimDTO process(ClaimProcessRequest claimProcessRequest) throws ClaimNotFoundException, ClaimProcessingException {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimProcessRequest.getClaimIdentifier())
                .orElseThrow(ClaimNotFoundException::new);

        StatusE status = claim.getStatus();
        ValidatorClaim.checkIsActionAllowed(claimProcessRequest.getAction(), status);

        if (claimProcessRequest.getAction().isPositive) {
            status = status.positiveProcess();

            historyService.archive(claimProcessRequest.getAction(), claim);
            addSharingNumberIfActionPublish(claimProcessRequest, claim);
        } else {
            ValidatorClaim.checkIsStringNull(claimProcessRequest.getOptionalReason());

            status = status.negativeProcess();
            historyService.archiveWithReason(claimProcessRequest.getAction(), claim, claimProcessRequest.getOptionalReason());
        }

        claim.setStatus(status);
        Claim updatedClaim = claimRepository.save(claim);

        return claimMapper.claimToDto(updatedClaim);

    }

    private void addSharingNumberIfActionPublish(ClaimProcessRequest claimProcessRequest, Claim claim) {
        if (ActionE.PUBLISH.equals(claimProcessRequest.getAction()))
            claim.setSharingNumber(SharingNumber.builder().uuid(UUID.randomUUID().toString()).build());
    }

}
