package net.pladema.sanitaryresponsibilityarea.application.port.output;

import net.pladema.sanitaryresponsibilityarea.domain.GetPatientCoordinatesByOutpatientConsultationFilterBo;
import net.pladema.sanitaryresponsibilityarea.domain.SanitaryRegionPatientMapCoordinatesBo;
import net.pladema.sanitaryresponsibilityarea.domain.GetPatientCoordinatesByAddedInstitutionFilterBo;

import java.util.List;

public interface PatientCoordinatesPort {

	List<SanitaryRegionPatientMapCoordinatesBo> getPatientCoordinatesByAddedInstitution(GetPatientCoordinatesByAddedInstitutionFilterBo filter);

	List<SanitaryRegionPatientMapCoordinatesBo> getPatientCoordinatesByOutpatientConsultation(GetPatientCoordinatesByOutpatientConsultationFilterBo filter);

}
