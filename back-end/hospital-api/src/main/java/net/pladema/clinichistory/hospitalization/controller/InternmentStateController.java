package net.pladema.clinichistory.hospitalization.controller;

import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.HealthConditionMapper;
import java.util.List;

import ar.lamansys.sgh.clinichistory.domain.ips.AllergyConditionBo;
import ar.lamansys.sgh.clinichistory.domain.ips.AnthropometricDataBo;
import ar.lamansys.sgh.clinichistory.domain.ips.DiagnosisBo;
import ar.lamansys.sgh.clinichistory.domain.ips.FamilyHistoryBo;
import ar.lamansys.sgh.clinichistory.domain.ips.HealthConditionBo;
import ar.lamansys.sgh.clinichistory.domain.ips.ImmunizationBo;
import ar.lamansys.sgh.clinichistory.domain.ips.Last2RiskFactorsBo;
import ar.lamansys.sgh.clinichistory.domain.ips.MedicationBo;
import ar.lamansys.sgh.clinichistory.domain.ips.PersonalHistoryBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.clinichistory.hospitalization.controller.dto.InternmentAnthropometricDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.FetchHospitalizationAllergyState;
import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.FetchHospitalizationClinicalObservationState;
import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.FetchHospitalizationGeneralState;
import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.FetchHospitalizationHealthConditionState;
import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.FetchHospitalizationImmunizationState;
import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.FetchHospitalizationMedicationState;
import ar.lamansys.sgh.clinichistory.application.fetchHospitalizationState.HospitalizationGeneralState;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.AllergyConditionDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.DiagnosesGeneralStateDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.DiagnosisDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.HealthConditionDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.HealthHistoryConditionDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.ImmunizationDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.Last2RiskFactorsDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.MedicationDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pladema.clinichistory.hospitalization.controller.constraints.InternmentValid;
import net.pladema.clinichistory.hospitalization.controller.dto.InternmentGeneralStateDto;
import net.pladema.clinichistory.hospitalization.controller.mapper.InternmentStateMapper;

@RestController
@RequestMapping("/institutions/{institutionId}/internments-state")
@Tag(name = "Internment states", description = "Internment states")
@Slf4j
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO_ADULTO_MAYOR, ENFERMERO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, PERSONAL_DE_IMAGENES, PERSONAL_DE_LABORATORIO, PERSONAL_DE_FARMACIA, PRESCRIPTOR, ABORDAJE_VIOLENCIAS')")
public class InternmentStateController {

    private static final String LOGGING_OUTPUT = "Output -> {}";
    private static final String LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE = "Input parameters -> institutionId {}, internmentEpisodeId {}";

    private final FetchHospitalizationGeneralState fetchHospitalizationGeneralState;

    private final FetchHospitalizationHealthConditionState fetchHospitalizationHealthConditionState;

    private final FetchHospitalizationMedicationState fetchHospitalizationMedicationState;

    private final FetchHospitalizationAllergyState fetchHospitalizationAllergyState;

    private final FetchHospitalizationImmunizationState fetchHospitalizationImmunizationState;

    private final FetchHospitalizationClinicalObservationState fetchHospitalizationClinicalObservationState;

    private final InternmentStateMapper internmentStateMapper;

    private final HealthConditionMapper healthConditionMapper;

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general")
    public ResponseEntity<InternmentGeneralStateDto> internmentGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId){
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        HospitalizationGeneralState interment = fetchHospitalizationGeneralState.getInternmentGeneralState(internmentEpisodeId);
        InternmentGeneralStateDto result = internmentStateMapper.toInternmentGeneralStateDto(interment);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/maindiagnosis")
    public ResponseEntity<HealthConditionDto> mainDiagnosisGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        HealthConditionBo mainDiagnosis = fetchHospitalizationHealthConditionState.getMainDiagnosisGeneralState(internmentEpisodeId);
        HealthConditionDto result = internmentStateMapper.toHealthConditionDto(mainDiagnosis);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/alternativeDiagnoses")
    public ResponseEntity<List<DiagnosisDto>> getAlternativeDiagnosesGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<DiagnosisBo> diagnosis = fetchHospitalizationHealthConditionState.getAlternativeDiagnosisGeneralState(internmentEpisodeId);
        List<DiagnosisDto> result = healthConditionMapper.toListDiagnosisDto(diagnosis);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/alternativeDiagnoses/active")
    public ResponseEntity<List<DiagnosisDto>> getActiveAlternativeDiagnosesGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<DiagnosisBo> diagnosis = fetchHospitalizationHealthConditionState.getActiveAlternativeDiagnosesGeneralState(internmentEpisodeId);
        List<DiagnosisDto> result = healthConditionMapper.toListDiagnosisDto(diagnosis);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/diagnoses")
    public ResponseEntity<List<DiagnosesGeneralStateDto>> getDiagnosesGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<HealthConditionBo> diagnoses = fetchHospitalizationHealthConditionState.getDiagnosesGeneralState(internmentEpisodeId);
        List<DiagnosesGeneralStateDto> result = internmentStateMapper.toListDiagnosesGeneralStateDto(diagnoses);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/personalHistories")
    public ResponseEntity<List<HealthHistoryConditionDto>> personalHistoriesGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<PersonalHistoryBo> personalHistories = fetchHospitalizationHealthConditionState.getPersonalHistoriesGeneralState(internmentEpisodeId);
        List<HealthHistoryConditionDto> result = internmentStateMapper.toListHealthHistoryConditionDtoFromPersonalHistoryBo(personalHistories);
                log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/familyHistories")
    public ResponseEntity<List<HealthHistoryConditionDto>> familyHistoriesGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<FamilyHistoryBo> familyHistories = fetchHospitalizationHealthConditionState.getFamilyHistoriesGeneralState(internmentEpisodeId);
        List<HealthHistoryConditionDto> result = internmentStateMapper.toListHealthHistoryConditionDtoFromFamilyHistoryBo(familyHistories);
                log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/medications")
    public ResponseEntity<List<MedicationDto>> medicationsGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<MedicationBo> medicationBos = fetchHospitalizationMedicationState.run(internmentEpisodeId);
        List<MedicationDto> result = internmentStateMapper.toListInternmentMedicationDto(medicationBos);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

	@InternmentValid
	@GetMapping("/{internmentEpisodeId}/general/last-2-anthropometric-data")
	public ResponseEntity<List<InternmentAnthropometricDataDto>> getLast2AnthropometricDataGeneralState(
			@PathVariable(name = "institutionId") Integer institutionId,
			@PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId){
		log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
		List<AnthropometricDataBo> anthropometricData = fetchHospitalizationClinicalObservationState.getLast2AnthropometricDataGeneralState(internmentEpisodeId);
		List<InternmentAnthropometricDataDto> result = internmentStateMapper.toListInternmentAnthropometricDataDto(anthropometricData);
		log.debug(LOGGING_OUTPUT, result);
		return  ResponseEntity.ok().body(result);
	}

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/riskFactors")
    public ResponseEntity<Last2RiskFactorsDto> riskFactorsGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId){
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        Last2RiskFactorsBo riskFactorBos = fetchHospitalizationClinicalObservationState.getLast2RiskFactorsGeneralState(internmentEpisodeId);
        Last2RiskFactorsDto result = internmentStateMapper.toLast2RiskFactorDto(riskFactorBos);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/immunizations")
    public ResponseEntity<List<ImmunizationDto>> immunizationsGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId){
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<ImmunizationBo> immunizationBos = fetchHospitalizationImmunizationState.run(internmentEpisodeId);
        List<ImmunizationDto> result = internmentStateMapper.toListImmunizationDto(immunizationBos);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

    @InternmentValid
    @GetMapping("/{internmentEpisodeId}/general/allergies")
    public ResponseEntity<List<AllergyConditionDto>> allergiesGeneralState(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId){
        log.debug(LOGGING_INSTITUTION_AND_INTERNMENT_EPISODE, institutionId, internmentEpisodeId);
        List<AllergyConditionBo> allergyConditionBos = fetchHospitalizationAllergyState.run(internmentEpisodeId);
        List<AllergyConditionDto> result = internmentStateMapper.toListAllergyConditionDto(allergyConditionBos);
        log.debug(LOGGING_OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }

}