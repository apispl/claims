package pl.pszczolkowski.claims.claimcore.dto;

import lombok.Data;
import pl.pszczolkowski.claims.claimcore.statemachine.StatusE;

@Data
public class ClaimRequestParams {

    private String identifier;
    private String name;
    private String content;
    private StatusE status;

    // sorting
    private String direction;
    private String sort;

    // pagination
    private Integer page;
    private Integer size;
}