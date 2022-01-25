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
import pl.pszczolkowski.claims.core.entities.History;
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
    private final HistoryRepository historyRepository;

    public ClaimFacade(ClaimRepository claimRepository, HistoryRepository historyRepository) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
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
