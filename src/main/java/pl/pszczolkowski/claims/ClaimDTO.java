package pl.pszczolkowski.claims;

import lombok.Builder;
import lombok.Data;
import pl.pszczolkowski.claims.entities.StatusE;

@Data
@Builder
class ClaimDTO {

 private Long id;
 private String name;
 private String content;
 private StatusE status;

}
