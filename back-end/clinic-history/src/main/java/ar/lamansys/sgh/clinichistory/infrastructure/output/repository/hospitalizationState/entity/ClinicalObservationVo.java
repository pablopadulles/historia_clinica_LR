package ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity;

import ar.lamansys.sgh.clinichistory.domain.ips.enums.ERiskFactor;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.entity.ObservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalObservationVo {

	private Integer id;

	private String sctidCode;

	private String statusId;

	private String value;

	private LocalDateTime effectiveTime;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClinicalObservationVo riskFactor = (ClinicalObservationVo) o;
		return Objects.equals(id, riskFactor.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public boolean isAnthropometricData(){
		return ERiskFactor.isCodeAnthropometricData(sctidCode);
	}

    public boolean hasError() {
		return statusId.equalsIgnoreCase(ObservationStatus.ERROR);
    }
}
