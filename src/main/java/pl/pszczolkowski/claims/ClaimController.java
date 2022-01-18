package pl.pszczolkowski.claims;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pszczolkowski.claims.core.ClaimService;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.exceptions.ClaimContentCannotBeChangedException;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;

@RestController
@RequestMapping("/claim")
@Log4j2
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<ClaimDTO> getClaimByName(@RequestParam String claimIdentifier) throws ClaimNotFoundException {
        log.info("Get Claim: {}", claimIdentifier);
        ClaimDTO claimDto = claimService.getClaimByIdentifier(claimIdentifier);
        return ResponseEntity.ok(claimDto);
    }

    @PostMapping
    public ResponseEntity<ClaimDTO> createClaim(@RequestBody ClaimRequest claimRequest) {
        log.info("Create Claim request: {}", claimRequest);
        ClaimDTO claimDTO = claimService.saveClaim(claimRequest);
        return ResponseEntity.ok(claimDTO);
    }

    @PatchMapping
    public ResponseEntity<ClaimDTO> editClaimContent(@RequestParam String claimIdentifier,
                                                     @RequestParam String claimContent) throws ClaimContentCannotBeChangedException, ClaimNotFoundException {
        log.info("Edit Claim content: {}", claimIdentifier);
        ClaimDTO claimDtoResponse = claimService.editClaim(claimIdentifier, claimContent);
        return ResponseEntity.ok(claimDtoResponse);
    }
}
