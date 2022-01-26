package pl.pszczolkowski.claims.core;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimProcessRequest;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.dto.ClaimRequestParams;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.entities.Claim;
import pl.pszczolkowski.claims.core.entities.SharingNumber;
import pl.pszczolkowski.claims.core.entities.StatusE;
import pl.pszczolkowski.claims.core.exceptions.ClaimContentCannotBeChangedException;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;
import pl.pszczolkowski.claims.core.exceptions.ReasonForActionRequired;

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

    public ClaimDTO editClaim(String claimIdentifier, String claimContent) throws ClaimContentCannotBeChangedException, ClaimNotFoundException {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimIdentifier)
                .orElseThrow(ClaimNotFoundException::new);

        //TODO validation only if claim status is CREATED, VERIFIED
        boolean isContentChanged = !claimContent.equals(claim.getContent());
        if (isContentChanged && claim.isNotInStatus(StatusE.CREATED) && claim.isNotInStatus(StatusE.VERIFIED)) {
            throw new ClaimContentCannotBeChangedException();
        }

        claimMapper.updateClaimFromDto(ClaimDTO.builder().content(claimContent).build(), claim);
        Claim updatedClaim = claimRepository.save(claim);

        historyService.archive(ActionE.EDIT, updatedClaim);
        return claimMapper.claimToDto(updatedClaim);
    }

    public ClaimDTO process(ClaimProcessRequest claimProcessRequest) throws ClaimNotFoundException, ClaimProcessingException, ReasonForActionRequired {
        Claim claim = claimRepository
                .findClaimByIdentifier(claimProcessRequest.getClaimIdentifier())
                .orElseThrow(ClaimNotFoundException::new);

        StatusE status = claim.getStatus();
        if (claimProcessRequest.getAction().isPositive) {
            status = status.positiveProcess();

            historyService.archive(claimProcessRequest.getAction(), claim);
            addSharingNumberIfActionPublish(claimProcessRequest, claim);
        } else {
            optionalReasonCannotBeNull(claimProcessRequest);

            status = status.negativeProcess();
            historyService.archiveWithReason(claimProcessRequest.getAction(), claim, claimProcessRequest.getOptionalReason());
        }

        claim.setStatus(status);
        Claim updatedClaim = claimRepository.save(claim);

        return claimMapper.claimToDto(updatedClaim);

    }

    private void optionalReasonCannotBeNull(ClaimProcessRequest claimProcessRequest) throws ReasonForActionRequired {
        if (claimProcessRequest.getOptionalReason() == null)
            throw new ReasonForActionRequired("Reason cannot be null.");
    }

    private void addSharingNumberIfActionPublish(ClaimProcessRequest claimProcessRequest, Claim claim) {
        if (ActionE.PUBLISH.equals(claimProcessRequest.getAction())){
            claim.setSharingNumber(SharingNumber.builder().uuid(UUID.randomUUID().toString()).build());
        }
    }

}
