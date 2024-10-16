package ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class RiskFactorDto implements Serializable {

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto systolicBloodPressure;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto diastolicBloodPressure;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto temperature;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto heartRate;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto respiratoryRate;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto bloodOxygenSaturation;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto bloodGlucose;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto glycosylatedHemoglobin;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto cardiovascularRisk;

    @Valid
    @Nullable
    private EffectiveClinicalObservationDto hematocrit;

    public boolean hasValues(){
        return (systolicBloodPressure != null
                || diastolicBloodPressure != null
                || temperature != null
                || heartRate != null
                || respiratoryRate != null
                || bloodOxygenSaturation != null
                || bloodGlucose != null
                || glycosylatedHemoglobin != null
                || cardiovascularRisk != null
                || hematocrit != null);
    }

    public boolean hasAnestheticClinicalEvaluationValues() {
        return systolicBloodPressure != null
                || diastolicBloodPressure != null
                || hematocrit != null;
    }
}
