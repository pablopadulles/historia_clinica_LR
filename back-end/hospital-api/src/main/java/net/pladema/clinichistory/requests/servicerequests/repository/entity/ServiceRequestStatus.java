package net.pladema.clinichistory.requests.servicerequests.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_request_status")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ServiceRequestStatus {

	public static final String ACTIVE = "55561003";
	public static final String ERROR = "723510000";
	public static final String COMPLETED = "255594003";
	public static final String CANCELLED = "89925002";

	@Id
	@Column(name = "id", length = 20)
	private String id;

	@Column(name = "description", nullable = false, length = 50)
	private String description;

}
