package pl.pszczolkowski.claims.claimcore.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pszczolkowski.claims.claimcore.statemachine.StatusE;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLAIM")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private StatusE status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sharing_number_id", referencedColumnName = "id")
    private SharingNumber sharingNumber;

    public boolean isInStatus(StatusE status) {
        return this.status.equals(status);
    }

    public boolean isNotInStatus(StatusE status) {
        return !this.status.equals(status);
    }
}
