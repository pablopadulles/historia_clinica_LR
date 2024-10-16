package net.pladema.clinichistory.hospitalization.service;

import ar.lamansys.sgx.shared.filestorage.infrastructure.input.rest.GeneratedBlobBo;
import net.pladema.clinichistory.hospitalization.repository.domain.EvolutionNoteDocument;
import net.pladema.clinichistory.hospitalization.repository.domain.InternmentEpisode;
import net.pladema.clinichistory.hospitalization.service.domain.InternmentSummaryBo;
import net.pladema.clinichistory.hospitalization.service.domain.PatientDischargeBo;
import net.pladema.clinichistory.hospitalization.service.impl.exceptions.GeneratePdfException;
import net.pladema.clinichistory.hospitalization.service.impl.exceptions.InternmentEpisodeNotFoundException;
import net.pladema.clinichistory.hospitalization.service.impl.exceptions.MoreThanOneConsentDocumentException;
import net.pladema.clinichistory.hospitalization.service.impl.exceptions.PatientNotFoundException;
import net.pladema.clinichistory.hospitalization.service.impl.exceptions.PersonNotFoundException;
import net.pladema.patient.service.domain.PatientMedicalCoverageBo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InternmentEpisodeService {

    void updateAnamnesisDocumentId(Integer internmentEpisodeId, Long anamnesisDocumentId);

    Optional<InternmentSummaryBo> getIntermentSummary(Integer internmentEpisodeId);

    Optional<Integer> getPatient(Integer internmentEpisodeId);

    void updateEpicrisisDocumentId(Integer intermentEpisodeId, Long id);

    EvolutionNoteDocument addEvolutionNote(Integer internmentEpisodeId, Long evolutionNoteId);

    boolean haveAnamnesis(Integer internmentEpisodeId);

    boolean haveEpicrisis(Integer internmentEpisodeId);

    boolean haveMedicalDischarge(Integer internmentEpisodeId);

    boolean haveEvolutionNoteAfterAnamnesis(Integer internmentEpisodeId);

	boolean haveUpdatesAfterEpicrisis(Integer internmentEpisodeId);

    boolean havePhysicalDischarge(Integer internmentEpisodeId);

    LocalDateTime getEntryDate(Integer internmentEpisodeId);

    boolean canCreateEpicrisis(Integer internmentEpisodeId);

	PatientDischargeBo saveMedicalDischarge(PatientDischargeBo patientDischargeBo);
 
    PatientDischargeBo saveAdministrativeDischarge(PatientDischargeBo patientDischarge);
    
    void updateInternmentEpisodeStatus(Integer internmentEpisodeId, Short statusId);

    InternmentEpisode getInternmentEpisode(Integer internmentEpisodeId, Integer institutionId);

	List<InternmentEpisode> findByBedId(Integer bedId);

	boolean existsActiveForBedId(Integer bedId);

	LocalDateTime getLastUpdateDateOfInternmentEpisode(Integer internmentEpisode);

	LocalDateTime updateInternmentEpisodeProbableDischargeDate(Integer internmentEpisode, LocalDateTime probableDischargeDate);
	
	Integer updateInternmentEpisodeBed(Integer internmentEpisode, Integer newBedId);

	Optional<PatientMedicalCoverageBo> getMedicalCoverage(Integer internmentEpisode);

	void deleteAnamnesisDocumentId(Integer internmentEpisodeId);

	void deleteEpicrisisDocumentId(Integer internmentEpisodeId);
	
	PatientDischargeBo savePatientPhysicalDischarge(Integer internmentEpisodeId);

	boolean haveMoreThanOneIntermentEpisodesFromPatients(List<Integer> patients);

	GeneratedBlobBo generateEpisodeDocumentType(Integer institutionId, Integer consentId, Integer internmentEpisodeId, List<String> procedures, String observations, String professionalId) throws GeneratePdfException, PatientNotFoundException, PersonNotFoundException, InternmentEpisodeNotFoundException;

	void existsConsentDocumentInInternmentEpisode(Integer internmentEpisodeId, Integer consentId) throws MoreThanOneConsentDocumentException;
    
	Integer getInternmentEpisodeSectorId(Integer internmentEpisodeId);

	Integer getInternmentEpisodeRoomId(Integer internmentEpisodeId);

}
