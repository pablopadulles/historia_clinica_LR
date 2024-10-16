import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Episode } from '../components/emergency-care-patients-summary/emergency-care-patients-summary.component';
import { TriageMasterDataService } from '@api-rest/services/triage-master-data.service';
import { EmergencyCareMasterDataService } from '@api-rest/services/emergency-care-master-data.service';
import { Observable } from 'rxjs';
import { MasterDataInterface } from '@api-rest/api-model';
import { tap } from 'rxjs/operators';
import { atLeastOneValueInFormGroup } from '@core/utils/form.utils';
import { PERSON, REMOVE_SUBSTRING_DNI } from '@core/constants/validation-constants';
import { PatientType } from '@historia-clinica/constants/summaries';
import { EstadosEpisodio } from '../constants/masterdata';
import { TriageCategory } from '../components/triage-chip/triage-chip.component';

const NO_INFO: MasterDataInterface<number> = {
	id: -1,
	description: 'No definido'
};

export class EpisodeFilterService {

	constructor(
		private readonly formBuilder: UntypedFormBuilder,
		private readonly triageMasterDataService: TriageMasterDataService,
		private readonly emergencyCareMasterDataService: EmergencyCareMasterDataService
	) {
		this.form = this.formBuilder.group({
			triage: [null],
			emergencyCareType: [null],
			patientId: [null],
			identificationNumber: [null, Validators.maxLength(PERSON.MAX_LENGTH.identificationNumber)],
			firstName: [null, Validators.maxLength(PERSON.MAX_LENGTH.firstName)],
			lastName: [null, Validators.maxLength(PERSON.MAX_LENGTH.lastName)],
			temporal: [null],
			emergencyCareTemporary: [null],
			administrativeDischarge: [null]
		});
	}

	private form: UntypedFormGroup;

	static filterByTriage(episode: Episode, filters: EpisodeFilters): boolean {
		return (filters.triage ? episode.triage.emergencyCareEpisodeListTriageDto.id === filters.triage : true);
	}

	static filterByEmergencyCareType(episode: Episode, filters: EpisodeFilters): boolean {
		if (!filters.emergencyCareType) {
			return true;
		}
		if (filters.emergencyCareType === NO_INFO.id) {
			return (!episode.type);
		}
		return (filters.emergencyCareType ? episode.type?.id === filters.emergencyCareType : true);
	}

	static filterByPatientId(episode: Episode, filters: EpisodeFilters): boolean {
		return (filters.patientId ? episode.patient?.id === filters.patientId : true);
	}

	static filterByIdentificationNumber(episode: Episode, filters: EpisodeFilters): boolean {
		const identificationNumberWithoutZeros = Number(filters.identificationNumber?.replace(REMOVE_SUBSTRING_DNI, '')).toString();
		return (filters.identificationNumber ?
			this.filterString(episode.patient?.person?.identificationNumber, identificationNumberWithoutZeros) : true);
	}

	static filterByFirstName(episode: Episode, filters: EpisodeFilters): boolean {
		return (filters.firstName ?
			this.filterString(episode.patient?.person?.firstName, filters.firstName) : true);
	}

	static filterByLastName(episode: Episode, filters: EpisodeFilters): boolean {
		return (filters.lastName ?
			this.filterString(episode.patient?.person?.lastName, filters.lastName) : true);
	}

	static filterString(target: string, filterValue: string): boolean {
		return target?.toLowerCase().includes(filterValue.toLowerCase());
	}

	static filterTemporal(episode: Episode, filters: EpisodeFilters): boolean {
		return (filters.temporal ? episode.patient?.typeId === PatientType.TEMPORARY : true);
	}

	static filterNoPatient(episode: Episode, filters: EpisodeFilters) {
		return (filters.emergencyCareTemporary ? episode.patient?.typeId === PatientType.EMERGENCY_CARE_TEMPORARY : true);
	}

	static filterAdministrativeDischarge(episode: Episode, filters: EpisodeFilters) {
		const administrativeDischarge = episode.state.id === EstadosEpisodio.CON_ALTA_ADMINISTRATIVA;
		return (filters.administrativeDischarge ?  administrativeDischarge : !administrativeDischarge);
	}

	getForm(): UntypedFormGroup {
		return this.form;
	}

	filter(episode: Episode): boolean {
		const filters = this.form.value as EpisodeFilters;
		return 	EpisodeFilterService.filterByTriage(episode, filters) &&
				EpisodeFilterService.filterByEmergencyCareType(episode, filters) &&
				EpisodeFilterService.filterByIdentificationNumber(episode, filters) &&
				EpisodeFilterService.filterByPatientId(episode, filters) &&
				EpisodeFilterService.filterByFirstName(episode, filters) &&
				EpisodeFilterService.filterByLastName(episode, filters) &&
				EpisodeFilterService.filterTemporal(episode, filters) &&
				EpisodeFilterService.filterNoPatient(episode, filters) &&
				EpisodeFilterService.filterAdministrativeDischarge(episode, filters);
	}

	clear(control: string): void {
		this.form.controls[control].reset();
	}

	markAsFiltered(): void {
		this.form.markAllAsTouched();
	}

	hasFilters(): boolean {
		return atLeastOneValueInFormGroup(this.form);
	}

	isValid(): boolean {
		return this.form.valid;
	}

	getTriageCategories(): Observable<TriageCategory[]> {
		return this.triageMasterDataService.getCategories();
	}

	getEmergencyCareTypes(): Observable<MasterDataInterface<number>[]> {
		return this.emergencyCareMasterDataService.getType().pipe(tap(types => types.unshift(NO_INFO)));
	}

}

interface EpisodeFilters {
	triage?: number;
	emergencyCareType?: number;
	patientId?: number;
	identificationNumber?: string;
	firstName?: string;
	lastName?: string;
	temporal?: boolean;
	emergencyCareTemporary?: boolean;
	administrativeDischarge?: boolean;
}
