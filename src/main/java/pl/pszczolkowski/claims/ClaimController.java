package pl.pszczolkowski.claims;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claim")
@Log4j2
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<Claim> createClaim(@RequestBody ClaimRequest claimRequest) {
        log.info("CreateClaim request: {}", claimRequest);
        Claim claimResponse = claimService.saveClaim(claimRequest);
        return ResponseEntity.ok(claimResponse);
    }
}
