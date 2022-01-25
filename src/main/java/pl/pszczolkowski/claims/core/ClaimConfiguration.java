package pl.pszczolkowski.claims.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClaimConfiguration {

    @Bean
    ClaimFacade claimFacade(ClaimRepository claimRepository, HistoryRepository historyRepository) {
        return new ClaimFacade(claimRepository, historyRepository);
    }
}
