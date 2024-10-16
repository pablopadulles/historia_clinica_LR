package net.pladema.medicalconsultation.appointment.service.fetchappointments.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DoctorBo {
	@ToString.Include
    private final String licenseNumber;

	@ToString.Include
	private final String firstName;

	@ToString.Include
	private final String lastName;

	@ToString.Include
	private final String identificationNumber;

	@ToString.Include
	private final Short genderId;

	public DoctorBo(String licenseNumber, String firstName, String lastName, String identificationNumber, Short genderId) {
		this.licenseNumber = licenseNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.identificationNumber = identificationNumber;
		this.genderId = genderId;
	}
}