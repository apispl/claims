package pl.pszczolkowski.claims.core;

import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.entities.Claim;
import pl.pszczolkowski.claims.core.entities.History;

import java.time.LocalDateTime;

class HistoryService {

    private final HistoryRepository historyRepository;

    HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    void archive(ActionE action, Claim claim) {
        historyRepository.save(History.builder()
                .claim(claim)
                .actionClaim(action)
                .actionTime(LocalDateTime.now())
                .build());
    }

    void archiveWithReason(ActionE action, Claim claim, String optionalReason) {
        historyRepository.save(History.builder()
                .claim(claim)
                .actionClaim(action)
                .optionalReason(optionalReason)
                .actionTime(LocalDateTime.now())
                .build());
    }
}
