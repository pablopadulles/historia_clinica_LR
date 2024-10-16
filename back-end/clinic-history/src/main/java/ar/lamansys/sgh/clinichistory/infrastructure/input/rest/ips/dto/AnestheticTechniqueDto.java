package ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto;

import java.util.List;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnestheticTechniqueDto extends ClinicalTermDto {

    @Nullable
    private Short techniqueId;

    @Nullable
    private Boolean trachealIntubation;

    @Nullable
    private List<Short> trachealIntubationMethodIds;

    @Nullable
    private Short breathingId;

    @Nullable
    private Short circuitId;

    @Nullable
    @JsonIgnore
    private String techniqueDescription;

    @Nullable
    @JsonIgnore
    private String trachealIntubationMethodDescription;

    @Nullable
    @JsonIgnore
    private String breathingDescription;

    @Nullable
    @JsonIgnore
    private String circuitDescription;
}
