package pl.pszczolkowski.claims.claimcore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {

    private String identifier;
    private String name;
    private String content;

}
