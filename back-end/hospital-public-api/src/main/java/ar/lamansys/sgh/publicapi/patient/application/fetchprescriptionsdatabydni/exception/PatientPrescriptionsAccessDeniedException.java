package ar.lamansys.sgh.publicapi.patient.application.fetchprescriptionsdatabydni.exception;

import ar.lamansys.sgh.publicapi.generalexceptions.PublicApiAccessDeniedException;

public class PatientPrescriptionsAccessDeniedException extends PublicApiAccessDeniedException {

	public PatientPrescriptionsAccessDeniedException() {
		super("PatientInformation", "PatientPrescriptionsDataController");
	}
}
