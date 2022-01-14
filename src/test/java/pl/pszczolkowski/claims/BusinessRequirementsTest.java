package pl.pszczolkowski.claims;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class BusinessRequirementsTest {

    @Autowired
    ClaimRepository claimRepository;

    @Test
    void shouldCreateClaim() {
        Claim claim = Claim.builder()
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimRepository.save(claim);

        Optional<Claim> governmentClaim = claimRepository.findClaimByName("Government Claim");

        assertThat(governmentClaim).isNotEmpty();
    }

    @Test
    void shouldNotCreateClaim_emptyNameOrContent() {
        Claim claim = Claim.builder().build();

        DataIntegrityViolationException dataIntegrityViolationException = assertThrows(DataIntegrityViolationException.class, () -> claimRepository.save(claim));

        assertNotNull(dataIntegrityViolationException);
    }

}
