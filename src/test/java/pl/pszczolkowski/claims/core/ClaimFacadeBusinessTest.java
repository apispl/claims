package pl.pszczolkowski.claims.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import pl.pszczolkowski.claims.core.dto.ClaimDTO;
import pl.pszczolkowski.claims.core.dto.ClaimProcessRequest;
import pl.pszczolkowski.claims.core.dto.ClaimRequest;
import pl.pszczolkowski.claims.core.entities.ActionE;
import pl.pszczolkowski.claims.core.entities.History;
import pl.pszczolkowski.claims.core.exceptions.ClaimContentCannotBeChangedException;
import pl.pszczolkowski.claims.core.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;
import pl.pszczolkowski.claims.core.exceptions.ReasonForActionRequired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(ClaimFacade.class)
class ClaimFacadeBusinessTest {

    @Autowired
    ClaimFacade claimFacade;

    @Autowired
    HistoryRepository historyRepository;

    @Test
    void shouldCreateClaim() throws ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimFacade.saveClaim(claimRequest);

        ClaimDTO claimDTO = claimFacade.getClaimByIdentifier("claim/1");

        assertThat(claimDTO.getContent()).isNotEmpty();
        assertThat(claimDTO.getName()).isNotEmpty();
    }

    @Test
    void shouldNotCreateClaim_emptyNameOrContent() {
        ClaimRequest claimRequest = ClaimRequest.builder().build();

        DataIntegrityViolationException dataIntegrityViolationException =
                assertThrows(DataIntegrityViolationException.class, () -> claimFacade.saveClaim(claimRequest));

        assertNotNull(dataIntegrityViolationException);
    }

    @Test
    void shouldEditClaim() throws ClaimContentCannotBeChangedException, ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        ClaimDTO claimDTO = claimFacade.saveClaim(claimRequest);

        ClaimDTO responseClaimDto = claimFacade.editClaim(claimDTO.getIdentifier(), "EDITED");

        assertThat(responseClaimDto.getContent()).isEqualTo("EDITED");
    }

    @Test
    void shouldNotEditClaim() throws ClaimNotFoundException, ClaimProcessingException, ReasonForActionRequired {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        ClaimDTO claimDTO = claimFacade.saveClaim(claimRequest);

        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .action(ActionE.VERIFY).build());
        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .action(ActionE.ACCEPT).build());

        claimDTO.setContent("EDITED");

        assertThrows(ClaimContentCannotBeChangedException.class, () -> claimFacade.editClaim(claimDTO.getIdentifier(), "EDITED"));
    }

    @Test
    void shouldSaveReason_WhenProcessNegative() throws ClaimNotFoundException, ClaimProcessingException, ReasonForActionRequired {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimFacade.saveClaim(claimRequest);

        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .optionalReason("reason")
                .action(ActionE.DELETE).build());

        List<History> allByClaim_identifier = historyRepository.findAllByClaim_Identifier("claim/1");

        assertThat(allByClaim_identifier)
                .anySatisfy(history -> {
                    assertThat(history.getOptionalReason()).isEqualTo("reason");
                });
    }

    @Test
    void shouldNotSaveReason_WhenProcessNegative() {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimFacade.saveClaim(claimRequest);

        assertThrows(ReasonForActionRequired.class, () -> claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .action(ActionE.DELETE).build()));
    }

    @Test
    void shouldAddSharingNumber_WhenPublish() throws ReasonForActionRequired, ClaimNotFoundException, ClaimProcessingException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimFacade.saveClaim(claimRequest);

        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .optionalReason("reason")
                .action(ActionE.VERIFY).build());

        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .optionalReason("reason")
                .action(ActionE.ACCEPT).build());

        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .optionalReason("reason")
                .action(ActionE.PUBLISH).build());

        ClaimDTO claimByIdentifier = claimFacade.getClaimByIdentifier("claim/1");

        assertThat(claimByIdentifier.getSharingNumber()).isNotNull();
    }
}
