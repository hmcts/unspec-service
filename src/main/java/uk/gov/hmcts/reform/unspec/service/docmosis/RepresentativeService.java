package uk.gov.hmcts.reform.unspec.service.docmosis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Representative;
import uk.gov.hmcts.reform.unspec.service.OrganisationService;
import uk.gov.hmcts.reform.unspec.service.flowstate.StateFlowEngine;

import static uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Representative.fromOrganisation;
import static uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Representative.fromSolicitorOrganisationDetails;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.fromFullName;

@Service
@RequiredArgsConstructor
public class RepresentativeService {

    private final StateFlowEngine stateFlowEngine;
    private final OrganisationService organisationService;

    public Representative getRespondentRepresentative(CaseData caseData) {
        var stateFlow = stateFlowEngine.evaluate(caseData).getState();
        var organisationId = caseData.getRespondent1OrganisationPolicy().getOrganisation().getOrganisationID();
        if (fromFullName(stateFlow.getName()) != PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT) {
            return fromOrganisation(organisationService.findOrganisationById(organisationId)
                                        .orElseThrow(RuntimeException::new));
        }
        return fromSolicitorOrganisationDetails(caseData.getRespondentSolicitor1OrganisationDetails());
    }
}
