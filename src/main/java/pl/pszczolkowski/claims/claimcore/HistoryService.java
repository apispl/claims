package pl.pszczolkowski.claims.claimcore;

import pl.pszczolkowski.claims.claimcore.enums.ActionE;
import pl.pszczolkowski.claims.claimcore.entities.Claim;
import pl.pszczolkowski.claims.claimcore.entities.History;

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
