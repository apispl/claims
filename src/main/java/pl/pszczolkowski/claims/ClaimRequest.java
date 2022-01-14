package pl.pszczolkowski.claims;

import lombok.Data;

@Data
class ClaimRequest {

    private final String name;
    private final String content;

}
