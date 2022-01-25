package pl.pszczolkowski.claims.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.pszczolkowski.claims.core.entities.Claim;

import java.util.Optional;

interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {

    Optional<Claim> findClaimByIdentifier(String identifier);
}

