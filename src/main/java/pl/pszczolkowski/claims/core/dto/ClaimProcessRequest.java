package pl.pszczolkowski.claims.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pszczolkowski.claims.core.entities.ActionE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimProcessRequest {

    private String claimIdentifier;
    private String optionalReason;
    private ActionE action;
}
