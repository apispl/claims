package pl.pszczolkowski.claims.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pszczolkowski.claims.core.entities.SharingNumber;
import pl.pszczolkowski.claims.core.entities.StatusE;

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
