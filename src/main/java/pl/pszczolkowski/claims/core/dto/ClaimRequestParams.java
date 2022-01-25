package pl.pszczolkowski.claims.core.dto;

import lombok.Data;
import pl.pszczolkowski.claims.core.entities.StatusE;

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