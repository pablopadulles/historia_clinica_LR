package ar.lamansys.sgh.clinichistory.infrastructure.input.service.mapper;

import ar.lamansys.sgh.clinichistory.domain.document.DocumentBo;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.AllergyConditionMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.AnthropometricDataMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.HealthConditionMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.DocumentHealthcareProfessionalMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.ImmunizationMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.MedicationMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.SnomedMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.mapper.RiskFactorMapper;
import ar.lamansys.sgh.clinichistory.infrastructure.input.service.dto.DocumentDto;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {AllergyConditionMapper.class, HealthConditionMapper.class, ImmunizationMapper.class, MedicationMapper.class,
        RiskFactorMapper.class, AnthropometricDataMapper.class, LocalDateMapper.class, SnomedMapper.class, DocumentHealthcareProfessionalMapper.class},
		builder = @Builder(disableBuilder = true))
public interface DocumentMapper {

    @Named("fromDocumentDto")
    @Mapping(target = "riskFactors", source = "riskFactors", qualifiedByName = "fromRiskFactorDto")
    @Mapping(target = "anthropometricData", source = "anthropometricData", qualifiedByName = "fromAnthropometricDataDto")
    @Mapping(target = "medications", source = "medications", qualifiedByName = "toListMedicationBo")
    @Mapping(target = "immunizations", source = "immunizations", qualifiedByName = "toListImmunizationBo")
    @Mapping(target = "allergies", source = "allergies", qualifiedByName = "toAllergyConditionReferableItemBo")
    @Mapping(target = "personalHistories", source = "personalHistories", qualifiedByName = "toReferablePersonalHistoryBoFromPersonalHistoryDto")
    @Mapping(target = "familyHistories", source = "familyHistories", qualifiedByName = "toReferableFamilyHistoryBoFromHealthHistory")
	@Mapping(target = "healthcareProfessionals", source = "healthcareProfessionals", qualifiedByName = "toDocumentHealthcareProfessionalBoList")
    DocumentBo fromDto(DocumentDto documentDto);

	@Named("fromDocumentBo")
	@Mapping(target = "riskFactors", source = "riskFactors", qualifiedByName = "fromRiskFactorBo")
	@Mapping(target = "anthropometricData", source = "anthropometricData", qualifiedByName = "fromAnthropometricDataBo")
	@Mapping(target = "medications", source = "medications", qualifiedByName = "toListMedicationDto")
	@Mapping(target = "immunizations", source = "immunizations", qualifiedByName = "toListImmunizationDto")
	@Mapping(target = "allergies", source = "allergies", qualifiedByName = "toAllergyConditionReferableItemDto")
	@Mapping(target = "personalHistories", source = "personalHistories", qualifiedByName = "toReferablePersonalHistoryDto")
	@Mapping(target = "familyHistories", source = "familyHistories", qualifiedByName = "toReferableFamilyHistoryConditionDto")
	DocumentDto fromBo(DocumentBo documentBo);
}
