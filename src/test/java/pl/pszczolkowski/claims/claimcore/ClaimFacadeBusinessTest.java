package pl.pszczolkowski.claims.claimcore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import pl.pszczolkowski.claims.claimcore.dto.ClaimDTO;
import pl.pszczolkowski.claims.claimcore.dto.ClaimProcessRequest;
import pl.pszczolkowski.claims.claimcore.dto.ClaimRequest;
import pl.pszczolkowski.claims.claimcore.enums.ActionE;
import pl.pszczolkowski.claims.claimcore.entities.History;
import pl.pszczolkowski.claims.claimcore.exceptions.ClaimNotFoundException;
import pl.pszczolkowski.claims.claimcore.exceptions.ClaimProcessingException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ClaimFacade.class, HistoryService.class})
class ClaimFacadeBusinessTest {

    @Autowired
    ClaimFacade claimFacade;

    @Autowired
    HistoryRepository historyRepository;

    @Test
    void shouldCreateClaim() throws ClaimNotFoundException {
        //given
        saveClaim();

        //when
        ClaimDTO claimDTO = claimFacade.getClaimByIdentifier("claim/1");

        //then
        assertThat(claimDTO.getContent()).isNotEmpty();
        assertThat(claimDTO.getName()).isNotEmpty();
    }

    @Test
    void shouldNotCreateClaim_emptyNameOrContent() {
        //given
        ClaimRequest claimRequest = ClaimRequest.builder().build();

        //when
        DataIntegrityViolationException dataIntegrityViolationException =
                assertThrows(DataIntegrityViolationException.class, () -> claimFacade.saveClaim(claimRequest));

        //then
        assertNotNull(dataIntegrityViolationException);
    }

    @Test
    void shouldEditClaim() throws ClaimNotFoundException, ClaimProcessingException {
        //given
        ClaimDTO claimDTO = saveClaim();

        //when
        ClaimDTO responseClaimDto = claimFacade.editClaim(claimDTO.getIdentifier(), "EDITED");

        //then
        assertThat(responseClaimDto.getContent()).isEqualTo("EDITED");
    }

    @Test
    void shouldNotEditClaim() throws ClaimNotFoundException, ClaimProcessingException {
        //given
        ClaimDTO claimDTO = saveClaim();

        //when
        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .action(ActionE.VERIFY).build());
        //and
        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .action(ActionE.ACCEPT).build());
        //and
        claimDTO.setContent("EDITED");

        //then
        assertThrows(ClaimProcessingException.class, () -> claimFacade.editClaim(claimDTO.getIdentifier(), "EDITED"));
    }

    @Test
    void shouldSaveReason_WhenProcessNegative() throws ClaimNotFoundException, ClaimProcessingException {
        //given
        saveClaim();

        //when
        claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .optionalReason("reason")
                .action(ActionE.DELETE).build());

        List<History> allByClaim_identifier = historyRepository.findAllByClaim_Identifier("claim/1");

        //then
        assertThat(allByClaim_identifier)
                .anySatisfy(history -> {
                    assertThat(history.getOptionalReason()).isEqualTo("reason");
                });
    }

    @Test
    void shouldNotSaveReason_WhenProcessNegative() {
        //given
        saveClaim();

        //when/then
        assertThrows(ClaimProcessingException.class, () -> claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .action(ActionE.DELETE).build()));
    }

    @Test
    void shouldAddSharingNumber_WhenPublish() throws ClaimNotFoundException, ClaimProcessingException {
        //given
        saveClaim();

        //when
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

        //then
        assertThat(claimByIdentifier.getSharingNumber()).isNotNull();
    }

    @Test
    void shouldNotProcessNotAllowedAction() {
        //given
        saveClaim();

        //when/then
        assertThrows(ClaimProcessingException.class, () -> claimFacade.process(ClaimProcessRequest.builder()
                .claimIdentifier("claim/1")
                .optionalReason("reason")
                .action(ActionE.ACCEPT).build()));
    }

    private ClaimDTO saveClaim() {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .identifier("claim/1")
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        return claimFacade.saveClaim(claimRequest);
    }
}
