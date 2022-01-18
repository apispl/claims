package pl.pszczolkowski.claims;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pszczolkowski.claims.core.ClaimProcessor;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;

@RestController
@RequestMapping("/claim/process")
@Log4j2
public class ProcessClaimController {

    private final ClaimProcessor claimProcessor;

    public ProcessClaimController(ClaimProcessor claimProcessor) {
        this.claimProcessor = claimProcessor;
    }

    @PatchMapping
    public ResponseEntity<ClaimDTO> processClaim(@RequestParam String claimIdentifier, @RequestParam ActionE actionE) throws ClaimNotFoundException, ClaimProcessingException {
        log.info("Process Claim: {}", claimIdentifier);
        claimProcessor.process(claimIdentifier, actionE);
        return null;
    }
}
