package net.pladema.medicalconsultation.equipmentdiary.service.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.medicalconsultation.diary.repository.entity.OpeningHours;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true, exclude = "id")
@NoArgsConstructor
public class EquipmentOpeningHoursBo extends TimeRangeBo {

    private Integer id;

    private Short dayWeekId;

    public EquipmentOpeningHoursBo(OpeningHours openingHours){
        super(openingHours.getFrom(), openingHours.getTo());
        this.dayWeekId = openingHours.getDayWeekId();
        this.id = openingHours.getId();
    }

	public boolean overlap(EquipmentOpeningHoursBo other) {
		return getDayWeekId().equals(other.getDayWeekId())
				&& getFrom().isBefore(other.getTo())
				&& getTo().isAfter(other.getFrom());
	}

}
