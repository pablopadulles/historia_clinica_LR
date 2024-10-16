package net.pladema.medicalconsultation.appointment.controller.dto;

import ar.lamansys.sgh.shared.infrastructure.input.service.ProfessionalPersonDto;

import java.time.LocalDateTime;

import ar.lamansys.sgx.shared.dates.controller.dto.DateTimeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import net.pladema.medicalconsultation.diary.controller.dto.DiaryLabelDto;

@Value
@Builder
@ToString
@AllArgsConstructor
public class AppointmentListDto {

	private final Integer id;

	private final AppointmentBasicPatientDto patient;

	private final String date;

	private final String hour;

	private final boolean overturn;

	private final Integer healthInsuranceId;

	private final String medicalCoverageName;

	private final String medicalCoverageAffiliateNumber;

	private final Short medicalAttentionTypeId;

	private final Short appointmentStateId;

	private final String phonePrefix;

	private final String phoneNumber;

	private final Short appointmentBlockMotiveId;

	private final boolean isProtected;

	private final LocalDateTime createdOn;

	private final DateTimeDto updatedOn;

	private final ProfessionalPersonDto professionalPersonDto;
	
	private final DiaryLabelDto diaryLabelDto;

	private String patientEmail;

	private boolean expiredRegister;

}
