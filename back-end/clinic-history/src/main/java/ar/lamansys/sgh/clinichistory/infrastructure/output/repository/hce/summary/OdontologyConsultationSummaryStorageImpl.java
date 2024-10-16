package ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hce.summary;

import ar.lamansys.sgh.clinichistory.application.ports.OdontologyConsultationSummaryStorage;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.ClinicalSpecialtyBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.DocumentDataBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.HealthConditionSummaryBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.HealthcareProfessionalBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.OdontologyEvolutionSummaryBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.ProcedureSummaryBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.ReasonSummaryBo;
import ar.lamansys.sgh.clinichistory.domain.hce.summary.ReferenceSummaryBo;
import ar.lamansys.sgh.clinichistory.domain.ips.SnomedBo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentStatus;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.entity.ConditionVerificationStatus;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.entity.ProblemType;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class OdontologyConsultationSummaryStorageImpl implements OdontologyConsultationSummaryStorage {

	private final List<Short> INVALID_REFERENCE_STATUS = List.of((short)3, (short)4);

    private final EntityManager entityManager;

	private final MapReferenceSummary mapReferenceSummary;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public List<OdontologyEvolutionSummaryBo> getAllOdontologyEvolutionSummary(Integer patientId) {
        String sqlString =" SELECT oc.id, oc.creationable.createdOn, "
                + "hp.id AS healthcarProfessionalId, hp.licenseNumber, hp.personId, "
                + "p.firstName, p.lastName, p.identificationNumber, pe.nameSelfDetermination, "
                + "cs.id AS clinicalSpecialtyId, cs.name AS clinicalSpecialtyName, cs.clinicalSpecialtyTypeId, "
                + "n.description,  docFile.id, docFile.filename, p.middleNames,p.otherLastNames"
                + " FROM OdontologyConsultation oc"
                + " LEFT JOIN ClinicalSpecialty cs ON (oc.clinicalSpecialtyId = cs.id)"
                + " JOIN Document doc ON (doc.sourceId = oc.id)"
                + " LEFT JOIN Note n ON (n.id = doc.evolutionNoteId)"
                + " JOIN HealthcareProfessional hp ON (hp.id = oc.doctorId)"
                + " JOIN Person p ON (p.id = hp.personId)"
				+ " LEFT JOIN PersonExtended pe ON (p.id = pe.id)"
                + " LEFT JOIN DocumentFile docFile ON (doc.id = docFile.id)"
                + " WHERE oc.billable = TRUE "
                + " AND oc.patientId = :patientId"
                + " AND doc.sourceTypeId = " + SourceType.ODONTOLOGY
                + " ORDER BY oc.performedDate DESC";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .getResultList();
        List<OdontologyEvolutionSummaryBo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new OdontologyEvolutionSummaryBo(
                        (Integer)a[0],
                        a[1] != null ? (LocalDateTime) a[1] : null,
                        new HealthcareProfessionalBo((Integer) a[2], (String) a[3], (Integer) a[4], (String) a[5], (String) a[6], (String) a[7], (String) a[8],(String) a[15], (String) a[16] ),
                        a[9] != null ? new ClinicalSpecialtyBo((Integer)a[9], (String)a[10], (Short) a[11]) : null,
                        (String)a[12],
                        a[13] != null ? new DocumentDataBo((Long)a[13], (String)a[14]) : null))
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HealthConditionSummaryBo> getHealthConditionsByPatient(Integer patientId, List<Integer> odontologyConsultationIds) {
        String sqlString ="SELECT hc.id, hc.patientId, "
                +"  s.sctid, s.pt, "
                +"  d.statusId, "
                +"  hc.startDate, hc.inactivationDate, "
                +"  hc.main, hc.problemId, oc.id, "
                +"  (hc.verificationStatusId = :verificationStatusError),"
                +"  hc.updateable.updatedOn AS timePerformedError,"
                +"  per.reasonId,"
                +"  n.description AS observationsError"
                +"  FROM OdontologyConsultation oc"
                +"  JOIN Document d ON (d.sourceId = oc.id)"
                +"  JOIN DocumentHealthCondition dhc ON (d.id = dhc.pk.documentId)"
                +"  JOIN HealthCondition hc ON (dhc.pk.healthConditionId = hc.id)"
                +"  JOIN Snomed s ON (s.id = hc.snomedId)"
                +"  LEFT JOIN ProblemErrorReason per ON (hc.id = per.healthConditionId)"
                +"  LEFT JOIN Note n ON (hc.noteId = n.id)"
                +"  WHERE d.statusId = '" + DocumentStatus.FINAL + "'"
                +"  AND d.sourceTypeId =" + SourceType.ODONTOLOGY
                +"  AND d.typeId = "+ DocumentType.ODONTOLOGY
                +"  AND hc.patientId = :patientId "
                +"  AND hc.problemId IN ('"+ ProblemType.PROBLEM+"', '"+ ProblemType.CHRONIC+ "')"
                +"  AND oc.id IN (:odontologyConsultationIds) ";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .setParameter("odontologyConsultationIds", odontologyConsultationIds)
                .setParameter("verificationStatusError", ConditionVerificationStatus.ERROR)
                .getResultList();
        List<HealthConditionSummaryBo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new HealthConditionSummaryBo(
                        (Integer)a[0],
                        (Integer)a[1],
                        new SnomedBo((String) a[2], (String) a[3]),
                        (String)a[4], null, null,
                        a[5] != null ? (LocalDate)a[5] : null,
                        a[6] != null ? (LocalDate)a[6] : null,
                        (boolean)a[7],
                        (String)a[8],
                        (Integer) a[9],
                        (Boolean) a[10],
                        (LocalDateTime) a[11],
                        (Short) a[12],
                        (String) a[13]))
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ReasonSummaryBo> getReasonsByPatient(Integer patientId, List<Integer> odontologyConsultationIds) {
        String sqlString = "SELECT r.id, r.description, oc.id"
                +"  FROM OdontologyConsultation oc"
                +"  JOIN OdontologyConsultationReason ocr ON (ocr.pk.odontologyConsultationId = oc.id)"
                +"  JOIN OdontologyReason r ON (r.id = ocr.pk.reasonId)"
                +"  WHERE oc.patientId = :patientId"
                +"  AND oc.id IN (:odontologyConsultationIds) ";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .setParameter("odontologyConsultationIds", odontologyConsultationIds)
                .getResultList();
        List<ReasonSummaryBo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new ReasonSummaryBo(
                        new SnomedBo((String) a[0],(String) a[1]),
                        (Integer)a[2]))
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProcedureSummaryBo> getProceduresByPatient(Integer patientId, List<Integer> odontologyConsultationIds) {
        String sqlString = "SELECT p.id, s.id, s.sctid, s.pt, p.performedDate, oc.id"
                +"  FROM OdontologyConsultation oc"
                +"  JOIN Document d ON (d.sourceId = oc.id)"
                +"  JOIN DocumentProcedure dp ON (d.id = dp.pk.documentId)"
                +"  JOIN Procedure p ON (dp.pk.procedureId = p.id)"
                +"  JOIN Snomed s ON (p.snomedId = s.id) "
                +"  WHERE oc.patientId = :patientId"
                +"  AND d.statusId = '" + DocumentStatus.FINAL + "'"
                +"  AND d.typeId = "+ DocumentType.ODONTOLOGY
                +"  AND d.sourceTypeId =" + SourceType.ODONTOLOGY
                +"  AND oc.id IN (:odontologyConsultationIds) ";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("patientId", patientId)
                .setParameter("odontologyConsultationIds", odontologyConsultationIds)
                .getResultList();
        List<ProcedureSummaryBo> result = new ArrayList<>();
        queryResult.forEach(a ->
                result.add(new ProcedureSummaryBo(
                        (Integer)a[0],
                        new SnomedBo((Integer) a[1],(String) a[2],(String) a[3]),
                        a[4] != null ? (LocalDate)a[4] : null,
                        (Integer) a[5]))
        );
        return result;
    }


    @Override
    public List<ReferenceSummaryBo> getReferencesByHealthCondition(Integer healthConditionId, Integer consultationId, List<Short> loggedUserRoleIds) {
        String sqlString = "SELECT DISTINCT r.id, cl.description, rn.description, i.name,"
                +" CASE WHEN hc.verificationStatusId = :healthConditionError THEN TRUE ELSE FALSE END AS cancelled"
                +"  FROM Reference r"
                +"  JOIN OdontologyConsultation oc ON (r.encounterId = oc.id)"
				+"  LEFT JOIN Institution i ON (r.destinationInstitutionId = i.id)"
                +"  LEFT JOIN CareLine cl ON (r.careLineId = cl.id)"
				+"  LEFT JOIN CareLineRole clr ON (clr.careLineId = cl.id)"
                +"  JOIN ReferenceHealthCondition rhc ON (r.id = rhc.pk.referenceId)"
                +"  JOIN HealthCondition hc ON (rhc.pk.healthConditionId = hc.id)"
                +"  LEFT JOIN ReferenceNote rn ON (r.referenceNoteId = rn.id)"
                +"  WHERE rhc.pk.healthConditionId = :healthConditionId"
                +"  AND r.sourceTypeId= " + SourceType.ODONTOLOGY
                +"  AND oc.id = :consultationId "
				+"  AND (cl.id IS NULL OR cl.classified IS FALSE OR (clr.roleId IN (:userRoles) AND cl.classified IS TRUE AND clr.deleteable.deleted IS FALSE)) "
				+"	AND r.statusId NOT IN :invalidReferenceStatus";

        List<Object[]> queryResult = entityManager.createQuery(sqlString)
                .setParameter("healthConditionId", healthConditionId)
                .setParameter("consultationId", consultationId)
                .setParameter("healthConditionError", ConditionVerificationStatus.ERROR)
				.setParameter("userRoles", loggedUserRoleIds)
				.setParameter("invalidReferenceStatus", INVALID_REFERENCE_STATUS)
                .getResultList();
        List<ReferenceSummaryBo> result = new ArrayList<>();
        queryResult.forEach(a -> result.add(mapReferenceSummary.processReferenceSummaryBo(a)));
        return result;
    }
}
