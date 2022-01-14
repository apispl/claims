package pl.pszczolkowski.claims;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findClaimByName(String name);
}

