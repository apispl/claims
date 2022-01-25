package pl.pszczolkowski.claims.core;

import org.springframework.data.jpa.domain.Specification;
import pl.pszczolkowski.claims.core.entities.Claim;
import pl.pszczolkowski.claims.core.entities.StatusE;

class ClaimSpecification {

    ClaimSpecification() {
    }

    public static Specification<Claim> claimIdentifierEquals(String claimIdentifier) {
        return (root, query, builder) -> {
            if (claimIdentifier != null) {
                return builder.equal(builder.lower(root.get("identifier")), claimIdentifier.toLowerCase());
            }
            return null;
        };
    }

    public static Specification<Claim> claimStatusEquals(StatusE statusE) {
        return (root, query, builder) -> {
            if (statusE != null) {
                return builder.equal(root.get("status"), statusE);
            }
            return null;
        };
    }
}
