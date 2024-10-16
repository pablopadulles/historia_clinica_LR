package ar.lamansys.nursing.infrastructure.input.rest.dto;


import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.SnomedDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.servicerequest.dto.CreateOutpatientServiceRequestDto;
import ar.lamansys.sgx.shared.dates.configuration.JacksonDateFormatConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NursingProcedureDto implements Serializable {

    @NotNull(message = "{value.mandatory}")
    @Valid
    @EqualsAndHashCode.Include
    private SnomedDto snomed;

    @Nullable
    @JsonFormat(pattern = JacksonDateFormatConfig.DATE_FORMAT)
    @EqualsAndHashCode.Include
    private String performedDate;

	@Nullable
	private CreateOutpatientServiceRequestDto serviceRequest;

}