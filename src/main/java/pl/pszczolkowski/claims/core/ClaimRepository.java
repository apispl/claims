package pl.pszczolkowski.claims.core;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pszczolkowski.claims.core.entities.Claim;

import java.util.Optional;

interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findClaimByIdentifier(String identifier);
}

