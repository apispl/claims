package pl.pszczolkowski.claims;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import pl.pszczolkowski.claims.entities.StatusE;
import pl.pszczolkowski.claims.exceptions.ClaimNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(ClaimService.class)
class BusinessRequirementsTest {

    @Autowired
    ClaimService claimService;

    @Test
    void shouldCreateClaim() throws ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        claimService.saveClaim(claimRequest);

        ClaimDTO claimDTO = claimService.getClaimByName("Government Claim");

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
    void shouldEditClaim() throws ClaimContentCannotBeChanged, ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        ClaimDTO claimDTO = claimService.saveClaim(claimRequest);

        claimDTO.setContent("EDITED");

        ClaimDTO responseClaimDto = claimService.editClaim(claimDTO);

        assertThat(responseClaimDto.getContent()).isEqualTo("EDITED");
    }

    @Test
    void shouldNotEditClaim() throws ClaimContentCannotBeChanged, ClaimNotFoundException {
        ClaimRequest claimRequest = ClaimRequest.builder()
                .name("Government Claim")
                .content("This is the content of Government Claim")
                .build();

        ClaimDTO claimDTO = claimService.saveClaim(claimRequest);

        claimDTO.setStatus(StatusE.ACCEPTED);
        claimService.editClaim(claimDTO);

        claimDTO.setContent("EDITED");

        assertThrows(ClaimContentCannotBeChanged.class, () -> claimService.editClaim(claimDTO));
    }
}
