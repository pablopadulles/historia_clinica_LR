package net.pladema.establishment.repository.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ar.lamansys.sgx.shared.auditable.listener.SGXAuditListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ar.lamansys.sgx.shared.auditable.entity.SGXAuditableEntity;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(SGXAuditListener.class)
@Table(name = "historic_patient_bed_relocation")
@Entity
public class HistoricPatientBedRelocation extends SGXAuditableEntity<Integer> {

    private static final long serialVersionUID = -2434800727451609960L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "origin_bed_id", nullable = false)
    private Integer originBedId;

    @Column(name = "destination_bed_id", nullable = false)
    private Integer destinationBedId;

    @Column(name = "internment_episode_id", nullable = false)
    private Integer internmentEpisodeId;

    @Column(name = "relocation_date", nullable = false)
    private LocalDateTime relocationDate;

    @Column(name = "origin_bed_free", nullable = false)
    private boolean originBedFree = true;

    public HistoricPatientBedRelocation(Integer originBedId, Integer destinationBedId) {
        this.originBedId = originBedId;
        this.destinationBedId = destinationBedId;
    }
}
