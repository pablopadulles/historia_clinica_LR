package ar.lamansys.refcounterref.application.updatereferenceregulationstate;

import ar.lamansys.refcounterref.application.port.HistoricReferenceRegulationStorage;
import ar.lamansys.refcounterref.application.port.ReferenceStorage;
import ar.lamansys.refcounterref.application.updatereferenceregulationstate.exceptions.UpdateReferenceRegulationStateException;
import ar.lamansys.refcounterref.application.updatereferenceregulationstate.exceptions.UpdateReferenceRegulationStateExceptionEnum;
import ar.lamansys.refcounterref.domain.enums.EReferenceRegulationState;
import ar.lamansys.refcounterref.infraestructure.output.repository.reference.Reference;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedServiceRequestPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateReferenceRegulationState {

	private final HistoricReferenceRegulationStorage historicReferenceRegulationStorage;
	private final ReferenceStorage referenceStorage;
	private final SharedServiceRequestPort sharedServiceRequestPort;

	public Boolean run(Integer referenceId, Short stateId, String reason){
		log.debug("Input parameters -> referenceId {}, stateId {}, reason {}", referenceId, stateId, reason);
		validateStateChange(stateId, reason);
		if (stateId.equals(EReferenceRegulationState.REJECTED.getId()))
			cancelServiceRequest(referenceId);
		Boolean result = historicReferenceRegulationStorage.updateReferenceRegulationState(referenceId, stateId, reason);
		log.debug("Output -> {}", result);
		return result;
	}

	private void validateStateChange(Short stateId, String reason){
		EReferenceRegulationState regulationState = EReferenceRegulationState.getById(stateId);
		if (regulationState.equals(EReferenceRegulationState.WAITING_APPROVAL))
			throw new UpdateReferenceRegulationStateException(UpdateReferenceRegulationStateExceptionEnum.INVALID_STATUS_ID, "El nuevo estado no es valido");
		if ((regulationState.equals(EReferenceRegulationState.REJECTED) || regulationState.equals(EReferenceRegulationState.SUGGESTED_REVISION)) && reason == null)
			throw new UpdateReferenceRegulationStateException(UpdateReferenceRegulationStateExceptionEnum.REASON_REQUIRED, "Se debe indicar un motivo");
		if (regulationState.equals(EReferenceRegulationState.APPROVED) && reason != null) {
			throw new UpdateReferenceRegulationStateException(UpdateReferenceRegulationStateExceptionEnum.REASON_MUST_BE_EMPTY, "No se debe indicar un motivo para aprobar la solicitud");
        }
	}

	private void cancelServiceRequest(Integer referenceId){
        referenceStorage.findById(referenceId).map(Reference::getServiceRequestId).ifPresent(sharedServiceRequestPort::cancelServiceRequest);
    }

}
