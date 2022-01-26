package pl.pszczolkowski.claims.claimcore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pszczolkowski.claims.claimcore.entities.SharingNumber;
import pl.pszczolkowski.claims.claimcore.statemachine.StatusE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {

 private Long id;
 private String identifier;
 private String name;
 private String content;
 private StatusE status;
 private SharingNumber sharingNumber;

}
