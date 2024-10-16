import { Component, OnInit, ElementRef } from '@angular/core';
import { UntypedFormBuilder, Validators, UntypedFormGroup, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { EAuditType, ERole } from '@api-rest/api-model';
import {
	APatientDto,
	BMPatientDto,
	EthnicityDto,
	PersonOccupationDto,
	EducationLevelDto,
	GenderDto,
	IdentificationTypeDto,
	PatientMedicalCoverageDto,
	PersonPhotoDto,
	SelfPerceivedGenderDto,
	BasicPatientDto
} from '@api-rest/api-model';
import { PatientService } from '@api-rest/services/patient.service';
import { scrollIntoError, hasError, VALIDATIONS, DEFAULT_COUNTRY_ID, updateControlValidator } from '@core/utils/form.utils';
import { PersonMasterDataService } from '@api-rest/services/person-master-data.service';
import { AddressMasterDataService } from '@api-rest/services/address-master-data.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { ContextService } from '@core/services/context.service';
import { MatDialog } from '@angular/material/dialog';
import { MedicalCoverageComponent, PatientMedicalCoverage } from '@pacientes/dialogs/medical-coverage/medical-coverage.component';
import { MapperService } from '@core/services/mapper.service';
import { PatientMedicalCoverageService } from '@api-rest/services/patient-medical-coverage.service';
import { PERSON } from '@core/constants/validation-constants';
import { PermissionsService } from '@core/services/permissions.service';
import { Observable, map, take } from 'rxjs';
import { PATTERN_INTEGER_NUMBER } from '@core/utils/pattern.utils';
import { dateISOParseDate, newDate } from '@core/utils/moment.utils';
import { fixDate } from '@core/utils/date/format';
import { decode, toParamsToSearchPerson } from '@pacientes/utils/search.utils';

const ROUTE_PROFILE = 'pacientes/profile/';
const ROUTE_HOME_PATIENT = 'pacientes';
const ROUTE_GUARD = 'guardia/nuevo-episodio/administrativa';

@Component({
	selector: 'app-new-patient',
	templateUrl: './new-patient.component.html',
	styleUrls: ['./new-patient.component.scss']
})
export class NewPatientComponent implements OnInit {

	readonly PERSON_MAX_LENGTH = PERSON.MAX_LENGTH;
	readonly GENDER_MAX_LENGTH = VALIDATIONS.MAX_LENGTH.gender;
	private readonly NONE_SELF_PERCEIVED_GENDER_SELECTED_ID = 10; // Dato Maestro proveniente de género autopercibido "Ninguna de las anteriores"

	public form: UntypedFormGroup;
	public personResponse: BMPatientDto;
	public formSubmitted = false;
	public today = newDate();
	public hasError = hasError;
	public genders: GenderDto[];
	public selfPerceivedGenders: SelfPerceivedGenderDto[];
	public showOtherGender: boolean = false;
	public countries: any[];
	public provinces: any[];
	public departments: any[];
	public cities: any[];
	public identificationTypeList: IdentificationTypeDto[];
	private readonly routePrefix;
	public patientType;
	public personPhoto: PersonPhotoDto;
	patientMedicalCoveragesToAdd: PatientMedicalCoverage[];
	public isSubmitButtonDisabled = false;
	public ethnicities: EthnicityDto[];
	public occupations: PersonOccupationDto[];
	public educationLevels: EducationLevelDto[];

	public identificationNumberDisabled = false;
	public identificationTypeIdDisabled = false;
	public genderIdDisabled = false;
	public firstNameDisabled = false;
	public middleNamesDisabled = false;
	public lastNameDisabled = false;
	public otherLastNamesDisabled = false;
	public birthDateDisabled = false;
	hasInstitutionalAdministrativeRole = false;
	hasToSaveFiles: boolean = false;
	patientId: number;
	personId: number;
	fromGuardModule = false;

	constructor(
		private formBuilder: UntypedFormBuilder,
		private router: Router,
		private el: ElementRef,
		private patientService: PatientService,
		private route: ActivatedRoute,
		private personMasterDataService: PersonMasterDataService,
		private addressMasterDataService: AddressMasterDataService,
		private snackBarService: SnackBarService,
		private contextService: ContextService,
		private dialog: MatDialog,
		private mapperService: MapperService,
		private patientMedicalCoverageService: PatientMedicalCoverageService,
		private permissionsService: PermissionsService,

	) {
		this.routePrefix = 'institucion/' + this.contextService.institutionId + '/';
	}

	ngOnInit(): void {
		this.route.queryParams
			.pipe(take(1), map(params => {
				let decodedParams = JSON.parse(decode(params.person));
				return toParamsToSearchPerson(decodedParams);
			}))
			.subscribe(paramsToSearchPerson => {
				this.fromGuardModule = paramsToSearchPerson.fromGuardModule;
				this.form = this.formBuilder.group({
					firstName: [paramsToSearchPerson.firstName ? paramsToSearchPerson.firstName : null, [Validators.required]],
					middleNames: [paramsToSearchPerson.middleNames ? paramsToSearchPerson.middleNames : null],
					lastName: [paramsToSearchPerson.lastName ? paramsToSearchPerson.lastName : null, [Validators.required]],
					otherLastNames: [paramsToSearchPerson.otherLastNames ? paramsToSearchPerson.otherLastNames : null],
					genderId: [Number(paramsToSearchPerson.genderId), [Validators.required]],
					identificationNumber: [paramsToSearchPerson.identificationNumber, [Validators.required, Validators.maxLength(VALIDATIONS.MAX_LENGTH.identif_number)]],
					identificationTypeId: [Number(paramsToSearchPerson.identificationTypeId), [Validators.required]],
					birthDate: [paramsToSearchPerson.birthDate ? dateISOParseDate(paramsToSearchPerson.birthDate) : null, [Validators.required]],

					// Person extended
					cuil: [paramsToSearchPerson.cuil, [Validators.pattern(PATTERN_INTEGER_NUMBER), Validators.maxLength(VALIDATIONS.MAX_LENGTH.cuil)]],
					mothersLastName: [],
					phonePrefix: [],
					phoneNumber: [],
					email: [null, Validators.email],
					ethnicityId: [],
					occupationId: [],
					educationLevelId: [],
					religion: [],
					nameSelfDetermination: [],
					genderSelfDeterminationId: [],
					otherGenderSelfDetermination: [{ value: null, disabled: true }, [Validators.required, Validators.maxLength(this.GENDER_MAX_LENGTH)]],

					// Address
					addressStreet: [],
					addressNumber: [],
					addressFloor: [],
					addressApartment: [],
					addressQuarter: [],
					addressCityId: { value: null, disabled: true },
					addressPostcode: [],

					addressProvinceId: [],
					addressCountryId: [],
					addressDepartmentId: { value: null, disabled: true },

					// doctors
					generalPractitioner: [],
					generalPractitionerPhoneNumber: [],
					pamiDoctor: [],
					pamiDoctorPhoneNumber: []
				});
				this.lockFormField(paramsToSearchPerson);
				this.patientType = paramsToSearchPerson.typeId;
				this.personPhoto = { imageData: paramsToSearchPerson.photo ? paramsToSearchPerson.photo : null };

				this.form.get("addressCountryId").valueChanges.subscribe(
					countryId => {
						this.clear(this.form.controls.addressProvinceId);
						delete this.provinces;
						if (countryId) {
							this.addressMasterDataService.getByCountry(countryId)
								.subscribe(provinces => {
									this.provinces = provinces;
								});
						}
					}
				);

				this.form.get("addressProvinceId").valueChanges.subscribe(
					provinceId => {
						this.clear(this.form.controls.addressDepartmentId);
						delete this.departments;
						if (provinceId) {
							this.addressMasterDataService.getDepartmentsByProvince(provinceId)
								.subscribe(departments => {
									this.departments = departments;
								});
						}
					}
				);

				this.form.get("addressDepartmentId").valueChanges.subscribe(
					departmentId => {
						this.clear(this.form.controls.addressCityId);
						delete this.cities;
						if (departmentId) {
							this.addressMasterDataService.getCitiesByDepartment(departmentId)
								.subscribe(cities => {
									this.cities = cities;
								});
						}
					}
				);

				this.form.get("addressCityId").valueChanges.subscribe(
					_ => {
						this.clear(this.form.controls.addressNumber);
						this.clear(this.form.controls.addressFloor);
						this.clear(this.form.controls.addressApartment);
						this.clear(this.form.controls.addressQuarter);
						this.clear(this.form.controls.addressStreet);
						this.clear(this.form.controls.addressPostcode);
					}
				);

			});

		this.personMasterDataService.getGenders()
			.subscribe(genders => {
				this.genders = genders;
			});

		this.personMasterDataService.getSelfPerceivedGenders()
			.subscribe(
				genders => this.selfPerceivedGenders = genders
			);

		this.personMasterDataService.getIdentificationTypes()
			.subscribe(identificationTypes => {
				this.identificationTypeList = identificationTypes;
			});

		this.personMasterDataService.getEthnicities()
			.subscribe(ethnicities => {
				this.ethnicities = ethnicities;
			});

		this.personMasterDataService.getOccupations()
			.subscribe(occupations => {
				this.occupations = occupations;
			});

		this.personMasterDataService.getEducationLevels()
			.subscribe(educationLevels => {
				this.educationLevels = educationLevels;
			});

		this.addressMasterDataService.getAllCountries()
			.subscribe(countries => {
				this.countries = countries;
				this.form.controls.addressCountryId.setValue(DEFAULT_COUNTRY_ID);
				this.setProvinces();
			});

		this.permissionsService.hasContextAssignments$([ERole.ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE, ERole.ADMINISTRADOR_INSTITUCIONAL_PRESCRIPTOR]).subscribe(hasInstitutionalAdministrativeRole => this.hasInstitutionalAdministrativeRole = hasInstitutionalAdministrativeRole);
	}

	private lockFormField(params) {
		if (params.identificationNumber) {
			this.form.controls.identificationNumber.disable();
			this.identificationNumberDisabled = true;
		}
		if (params.identificationTypeId) {
			this.form.controls.identificationTypeId.disable();
			this.identificationTypeIdDisabled = true;
		}
		if (params.genderId) {
			this.form.controls.genderId.disable();
			this.genderIdDisabled = true;
		}
		if (params.firstName) {
			this.form.controls.firstName.disable();
			this.firstNameDisabled = true;
			this.form.controls.middleNames.disable();
			this.middleNamesDisabled = true;
		}
		if (params.lastName) {
			this.form.controls.lastName.disable();
			this.lastNameDisabled = true;
			this.form.controls.otherLastNames.disable();
			this.otherLastNamesDisabled = true;
		}
		if (params.birthDate) {
			this.form.controls.birthDate.disable();
			this.birthDateDisabled = true;
		}
	}

	save(): void {
		this.formSubmitted = true;
		if (this.form.valid) {
			this.isSubmitButtonDisabled = true;
			const personRequest: APatientDto = this.mapToPersonRequest();
			this.patientService.addPatient(personRequest)
				.subscribe(patientId => {
					this.patientId = patientId;
					this.patientService.getPatientBasicData<BasicPatientDto>(patientId).subscribe((patientBasicData: BasicPatientDto) => {
						this.personId = patientBasicData.person.id;
						this.hasToSaveFiles = true;

					if (this.personPhoto != null) {
						this.patientService.addPatientPhoto(patientId, this.personPhoto).subscribe();
					}

					if (this.patientMedicalCoveragesToAdd) {
						const patientMedicalCoveragesDto: PatientMedicalCoverageDto[] =
							this.patientMedicalCoveragesToAdd.map(s => this.mapperService.toPatientMedicalCoverageDto(s));
						this.patientMedicalCoverageService.addPatientMedicalCoverages(patientId, patientMedicalCoveragesDto)
							.subscribe();
					}

					if(this.fromGuardModule)
						this.redirectToGuard();
					});
				}, _ => {
					this.isSubmitButtonDisabled = false;
					this.snackBarService.showError(this.getMessagesError());
				});
		} else {
			scrollIntoError(this.form, this.el);
		}
	}

	private redirectToGuard() {
		this.router.navigate([this.routePrefix + ROUTE_GUARD], {
			queryParams: {
				patientId: this.patientId
			}
		});
	}

	private getMessagesSuccess(): string {
		return this.hasInstitutionalAdministrativeRole ? 'pacientes.new.messages.SUCCESS_PERSON' : 'pacientes.new.messages.SUCCESS_PATIENT';
	}

	private getMessagesError(): string {
		return this.hasInstitutionalAdministrativeRole ? 'pacientes.new.messages.ERROR_PERSON' : 'pacientes.new.messages.ERROR_PATIENT';
	}

	subscribeFinishUploadFiles(filesId$: Observable<number[]>) {
		filesId$?.subscribe((filesIds: number[]) => {
			if (filesIds.length) {
				this.router.navigate([this.routePrefix + ROUTE_PROFILE + this.patientId]);
				this.snackBarService.showSuccess(this.getMessagesSuccess());
			}
		})
	}

	private mapToPersonRequest(): APatientDto {
		const patient: APatientDto = {
			birthDate: fixDate(this.form.controls.birthDate.value),
			firstName: this.form.controls.firstName.value,
			genderId: this.form.controls.genderId.value,
			identificationTypeId: this.form.controls.identificationTypeId.value,
			identificationNumber: this.form.controls.identificationNumber.value,
			lastName: this.form.controls.lastName.value,
			middleNames: this.form.controls.middleNames.value && this.form.controls.middleNames.value.length ? this.form.controls.middleNames.value : null,
			otherLastNames: this.form.controls.otherLastNames.value && this.form.controls.otherLastNames.value.length ? this.form.controls.otherLastNames.value : null,
			personAge: null,
			// Person extended
			cuil: this.form.controls.cuil.value,
			email: this.form.controls.email.value,
			ethnicityId: this.form.controls.ethnicityId.value,
			educationLevelId: this.form.controls.educationLevelId.value,
			occupationId: this.form.controls.occupationId.value,
			genderSelfDeterminationId: this.form.controls.genderSelfDeterminationId.value,
			mothersLastName: this.form.controls.mothersLastName.value,
			nameSelfDetermination: this.form.controls.nameSelfDetermination.value,
			phonePrefix: this.form.controls.phonePrefix.value,
			phoneNumber: this.form.controls.phoneNumber.value,
			religion: this.form.controls.religion.value,
			// Address
			apartment: this.form.controls.addressApartment.value,
			cityId: this.form.controls.addressCityId.value,
			floor: this.form.controls.addressFloor.value,
			number: this.form.controls.addressNumber.value,
			postcode: this.form.controls.addressPostcode.value,
			quarter: this.form.controls.addressQuarter.value,
			street: this.form.controls.addressStreet.value,
			countryId: this.form.controls.addressCountryId.value,
			departmentId: this.form.controls.addressDepartmentId.value,
			provinceId: this.form.controls.addressProvinceId.value,
			// Patient
			typeId: this.patientType,
			comments: null,
			identityVerificationStatusId: null,
			// doctors
			generalPractitioner: {
				fullName: this.form.controls.generalPractitioner.value,
				phoneNumber: this.form.controls.generalPractitionerPhoneNumber.value,
				generalPractitioner: true
			},
			pamiDoctor: {
				fullName: this.form.controls.pamiDoctor.value,
				phoneNumber: this.form.controls.pamiDoctorPhoneNumber.value,
				generalPractitioner: false

			},
			// Select for an audict
			auditType: EAuditType.UNAUDITED,
			fileIds: []
		};

		if (patient.genderSelfDeterminationId === this.NONE_SELF_PERCEIVED_GENDER_SELECTED_ID)
			patient.otherGenderSelfDetermination = this.form.value.otherGenderSelfDetermination;

		return patient;

	}

	setProvinces() {
		const countryId: number = this.form.controls.addressCountryId.value;
		this.addressMasterDataService.getByCountry(countryId)
			.subscribe(provinces => {
				this.provinces = provinces;
			});
	}

	setDepartments() {
		const provinceId: number = this.form.controls.addressProvinceId.value;
		this.addressMasterDataService.getDepartmentsByProvince(provinceId)
			.subscribe(departments => {
				this.departments = departments;
			});
		this.form.controls.addressDepartmentId.enable();
	}

	setCities() {
		const departmentId: number = this.form.controls.addressDepartmentId.value;
		this.addressMasterDataService.getCitiesByDepartment(departmentId)
			.subscribe(cities => {
				this.cities = cities;
			});
		this.form.controls.addressCityId.enable();
	}

	openMedicalCoverageDialog(): void {
		const dialogRef = this.dialog.open(MedicalCoverageComponent, {
			data: {
				genderId: this.form.getRawValue().genderId,
				identificationNumber: this.form.getRawValue().identificationNumber,
				identificationTypeId: this.form.getRawValue().identificationTypeId,
				initValues: this.patientMedicalCoveragesToAdd
			}
		});
		dialogRef.afterClosed().subscribe(medicalCoverages => {
			if (medicalCoverages) {
				this.patientMedicalCoveragesToAdd = medicalCoverages.patientMedicalCoverages;
			}
		});

	}

	goBack(): void {
		this.formSubmitted = false;
		const route = this.fromGuardModule ? `${this.routePrefix}${ROUTE_GUARD}` : `${this.routePrefix}${ROUTE_HOME_PATIENT}`;
		this.router.navigate([route]);
	}

	public showOtherSelfPerceivedGender(): void {
		this.showOtherGender = (this.form.value.genderSelfDeterminationId === this.NONE_SELF_PERCEIVED_GENDER_SELECTED_ID);
		this.form.get('otherGenderSelfDetermination').setValue(null);
		if (this.showOtherGender)
			this.form.get('otherGenderSelfDetermination').enable();
		else
			this.form.get('otherGenderSelfDetermination').disable();
	}

	public clearGenderSelfDetermination(): void {
		this.form.controls.genderSelfDeterminationId.reset();
		this.showOtherSelfPerceivedGender();
	}

	clear(control: AbstractControl): void {
		control.reset();
	}

	updatePhoneValidators() {
		if (this.form.controls.phoneNumber.value || this.form.controls.phonePrefix.value) {
			updateControlValidator(this.form, 'phoneNumber', [Validators.required, Validators.pattern(PATTERN_INTEGER_NUMBER), Validators.maxLength(VALIDATIONS.MAX_LENGTH.phone)]);
			updateControlValidator(this.form, 'phonePrefix', [Validators.required, Validators.pattern(PATTERN_INTEGER_NUMBER), Validators.maxLength(VALIDATIONS.MAX_LENGTH.phonePrefix)]);
		} else {
			updateControlValidator(this.form, 'phoneNumber', []);
			updateControlValidator(this.form, 'phonePrefix', []);
		}

	}
}
