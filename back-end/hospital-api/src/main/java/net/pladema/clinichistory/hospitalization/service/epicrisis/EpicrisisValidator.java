package net.pladema.clinichistory.hospitalization.service.epicrisis;

import ar.lamansys.sgh.clinichistory.application.document.validators.GeneralDocumentValidator;
import net.pladema.clinichistory.hospitalization.repository.domain.InternmentEpisode;
import net.pladema.clinichistory.hospitalization.service.InternmentEpisodeService;
import net.pladema.clinichistory.hospitalization.service.documents.validation.InternmentDocumentValidator;
import net.pladema.clinichistory.hospitalization.service.epicrisis.domain.EpicrisisBo;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintViolationException;
import java.util.Collections;

@Component
public class EpicrisisValidator extends InternmentDocumentValidator {

	private final InternmentEpisodeService internmentEpisodeService;

	public EpicrisisValidator(InternmentEpisodeService internmentEpisodeService,
							  GeneralDocumentValidator generalDocumentValidator) {
		super(generalDocumentValidator);
		this.internmentEpisodeService = internmentEpisodeService;
	}

    public void assertInternmentEpisodeCanCreateEpicrisis(InternmentEpisode internmentEpisode) {
		if(!internmentEpisodeService.canCreateEpicrisis(internmentEpisode.getId())) {
			throw new ConstraintViolationException("Esta internación no puede crear una epicrisis", Collections.emptySet());
		}
	}

	public void assertEpicrisisValid(EpicrisisBo epicrisis) {
		epicrisis.validateSelf();
		super.assertDocumentValid(epicrisis);
	}

}
