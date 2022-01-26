package pl.pszczolkowski.claims.claimcore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClaimConfiguration {

    @Bean
    ClaimFacade claimFacade(ClaimRepository claimRepository, HistoryRepository historyRepository) {
        HistoryService historyService = new HistoryService(historyRepository);

        return new ClaimFacade(claimRepository, historyService);
    }
}
