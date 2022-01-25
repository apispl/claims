package pl.pszczolkowski.claims;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pszczolkowski.claims.core.ClaimFacade;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimProcessRequest;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.dto.ClaimRequestParams;
import pl.pszczolkowski.claims.core.exceptions.ClaimContentCannotBeChangedException;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;
import pl.pszczolkowski.claims.core.exceptions.ReasonForActionRequired;
import pl.pszczolkowski.claims.utils.PageDefaultUtils;

@RestController
@RequestMapping("/claim")
@Log4j2
class ClaimController {

    private final ClaimFacade claimFacade;

    public ClaimController(ClaimFacade claimFacade) {
        this.claimFacade = claimFacade;
    }

    @GetMapping
    public ResponseEntity<ClaimDTO> getClaimByIdentifier(@RequestParam String claimIdentifier) throws ClaimNotFoundException {
        log.info("Get Claim: {}", claimIdentifier);
        ClaimDTO claimDto = claimFacade.getClaimByIdentifier(claimIdentifier);
        return ResponseEntity.ok(claimDto);
    }

    @GetMapping("/page")
    public Page<ClaimDTO> getClaims(@RequestBody ClaimRequestParams params) {
        log.info("Get Claim: {}", params);
        Pageable pageable = PageDefaultUtils.getPageWithDefaults(params.getPage(), params.getSize(), params.getDirection(), params.getSort());

        return claimFacade.getClaims(params, pageable);
    }

    @PostMapping
    public ResponseEntity<ClaimDTO> createClaim(@RequestBody ClaimRequest claimRequest) {
        log.info("Create Claim request: {}", claimRequest);
        ClaimDTO claimDTO = claimFacade.saveClaim(claimRequest);

        return ResponseEntity.ok(claimDTO);
    }

    @PatchMapping
    public ResponseEntity<ClaimDTO> editClaimContent(@RequestParam String claimIdentifier,
                                                     @RequestParam String claimContent) throws ClaimContentCannotBeChangedException, ClaimNotFoundException {
        log.info("Edit Claim content: {}", claimIdentifier);
        ClaimDTO claimDtoResponse = claimFacade.editClaim(claimIdentifier, claimContent);

        return ResponseEntity.ok(claimDtoResponse);
    }

    @PatchMapping("/process")
    public ResponseEntity<ClaimDTO> processClaim(@RequestBody ClaimProcessRequest claimProcessRequest) throws ClaimNotFoundException, ClaimProcessingException, ReasonForActionRequired {
        log.info("Process Claim: {}", claimProcessRequest);
        ClaimDTO result = claimFacade.process(claimProcessRequest);
        return ResponseEntity.ok(result);
    }
}
