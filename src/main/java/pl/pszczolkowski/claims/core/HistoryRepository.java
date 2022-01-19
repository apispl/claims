package pl.pszczolkowski.claims.core;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pszczolkowski.claims.core.entities.History;

import java.util.List;

interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findAllByClaim_Identifier(String claimIdentifier);
}
