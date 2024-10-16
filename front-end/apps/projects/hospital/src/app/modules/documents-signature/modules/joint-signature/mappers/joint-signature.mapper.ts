import { ElectronicSignatureInvolvedDocumentDto } from "@api-rest/api-model"
import { convertDateTimeDtoToDate } from "@api-rest/mapper/date-dto.mapper"
import { getDocumentType } from "@core/constants/summaries"
import { dateToViewDate } from "@core/utils/date.utils"
import { Detail } from "@presentation/components/details-section-custom/details-section-custom.component"
import { ShowMoreConceptsPipe } from "@presentation/pipes/show-more-concepts.pipe"
import { capitalize, capitalizeSentence } from "@core/utils/core.utils"
import { RegisterEditor } from "@presentation/components/register-editor-info/register-editor-info.component"
import { SummaryMultipleSignData } from "../../../components/summary-multiple-sign/summary-multiple-sign.component"
import { SummaryItem } from "../../../components/summary-list-multiple-sign/summary-list-multiple-sign.component"

export const buildSummaryItemCard = (documents: ElectronicSignatureInvolvedDocumentDto[]): SummaryItem[] => {
	return documents.map(document => {
		return {
			id: document.documentId,
			data: buildSummaryMultipleSignData(document)
		}
	})
}

export const buildSummaryMultipleSignData = (document: ElectronicSignatureInvolvedDocumentDto): SummaryMultipleSignData => {
	const showMoreConceptsPipe = new ShowMoreConceptsPipe();
	return {
		title: getDocumentType(document.documentTypeId).title,
		patient: capitalizeSentence(document.patientCompleteName),
		problem: document.problems.length ? capitalize(showMoreConceptsPipe.transform(document.problems)) : 'digital-signature.card-information.NO_SNOMED_CONCEPT',
		registerEditor: buildRegisterEditor(document),
		signStatus: document.signatureStatus
	}
}

export const buildRegisterEditor = (document: ElectronicSignatureInvolvedDocumentDto): RegisterEditor => {
	return {
		createdBy: capitalizeSentence(document.responsibleProfessionalCompleteName),
		date: convertDateTimeDtoToDate(document.documentCreationDate)
	}
}

export const buildHeaderInformation = (document: ElectronicSignatureInvolvedDocumentDto): Detail[] => {
	return [
		{
			title: 'firma-conjunta.details.AMBIT',
			text: getDocumentType(document.documentTypeId).title
		},
		{
			title: 'firma-conjunta.details.PATIENT',
			text: capitalizeSentence(document.patientCompleteName)
		},
		{
			title: 'firma-conjunta.details.DATE',
			text: dateToViewDate(convertDateTimeDtoToDate(document.documentCreationDate))
		},
		{
			title: 'firma-conjunta.details.PROFESSIONAL',
			text: capitalizeSentence(document.responsibleProfessionalCompleteName)
		},
	]
}
