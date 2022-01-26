package pl.pszczolkowski.claims.claimcore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pszczolkowski.claims.claimcore.enums.ActionE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimProcessRequest {

    private String claimIdentifier;
    private String optionalReason;
    private ActionE action;
}
