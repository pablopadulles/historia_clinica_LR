package net.pladema.clinichistory.requests.transcribed.infrastructure.output.repository;


import java.util.List;

public interface ListTranscribedDiagnosticReportRepository {
    List<Integer> execute(Integer patientId);
	Integer getByAppointmentId(Integer appointmentId);
	List<Object[]> getListTranscribedOrder(Integer patientId);
}
