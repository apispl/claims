package pl.pszczolkowski.claims;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import pl.pszczolkowski.claims.core.ClaimProcessor;
import pl.pszczolkowski.claims.core.ClaimService;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.exceptions.ClaimContentCannotBeChangedException;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ClaimService.class, ClaimProcessor.class})
class BusinessRequirementsTest {

    @Autowired
    ClaimService claimService;

    @Autowired
    ClaimProcessor claimProcessor;

    @Test
    void shouldCreateClaim() throws ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimService.saveClaim(claimRequest);

        ClaimDTO claimDTO = claimService.getClaimByIdentifier("claim/1");

        assertThat(claimDTO.getContent()).isNotEmpty();
        assertThat(claimDTO.getName()).isNotEmpty();
    }

    @Test
    void shouldNotCreateClaim_emptyNameOrContent() {
        ClaimRequest claimRequest = ClaimRequest.builder().build();

        DataIntegrityViolationException dataIntegrityViolationException =
                assertThrows(DataIntegrityViolationException.class, () -> claimService.saveClaim(claimRequest));

        assertNotNull(dataIntegrityViolationException);
    }

    @Test
    void shouldEditClaim() throws ClaimContentCannotBeChangedException, ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        ClaimDTO claimDTO = claimService.saveClaim(claimRequest);

        ClaimDTO responseClaimDto = claimService.editClaim(claimDTO.getIdentifier(), "EDITED");

        assertThat(responseClaimDto.getContent()).isEqualTo("EDITED");
    }

    @Test
    void shouldNotEditClaim() throws ClaimNotFoundException, ClaimProcessingException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        ClaimDTO claimDTO = claimService.saveClaim(claimRequest);

        claimProcessor.process("claim/1", ActionE.VERIFY);
        claimProcessor.process("claim/1", ActionE.ACCEPT);

        claimDTO.setContent("EDITED");

        assertThrows(ClaimContentCannotBeChangedException.class, () -> claimService.editClaim(claimDTO.getIdentifier(), "EDITED"));
    }
}
