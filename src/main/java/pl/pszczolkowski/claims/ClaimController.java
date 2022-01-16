package pl.pszczolkowski.claims;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.pszczolkowski.claims.entities.Claim;
import pl.pszczolkowski.claims.exceptions.ClaimNotFoundException;

@RestController
@RequestMapping("/claim")
@Log4j2
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<ClaimDTO> getClaimByName(@RequestParam String claimName) throws ClaimNotFoundException {
        log.info("Get Claim: {}", claimName);
        ClaimDTO claimDto = claimService.getClaimByName(claimName);
        return ResponseEntity.ok(claimDto);
    }

    @PostMapping
    public ResponseEntity<ClaimDTO> createClaim(@RequestBody ClaimRequest claimRequest) {
        log.info("Create Claim request: {}", claimRequest);
        ClaimDTO claimDTO = claimService.saveClaim(claimRequest);
        return ResponseEntity.ok(claimDTO);
    }

    @PutMapping
    public ResponseEntity<ClaimDTO> editClaim(@RequestBody ClaimDTO claimDTO) throws ClaimContentCannotBeChanged, ClaimNotFoundException {
        log.info("Edit Claim request: {}", claimDTO);
        ClaimDTO claimDtoResponse = claimService.editClaim(claimDTO);
        return ResponseEntity.ok(claimDtoResponse);
    }
}
