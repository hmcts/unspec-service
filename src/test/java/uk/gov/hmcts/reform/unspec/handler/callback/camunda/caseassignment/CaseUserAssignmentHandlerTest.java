package uk.gov.hmcts.reform.unspec.handler.callback.camunda.caseassignment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.ccd.model.Organisation;
import uk.gov.hmcts.reform.ccd.model.OrganisationPolicy;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.enums.CaseRole;
import uk.gov.hmcts.reform.unspec.handler.callback.BaseCallbackHandlerTest;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.CoreCaseUserService;

import java.util.Map;

import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
    CaseUserAssignmentHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
class CaseUserAssignmentHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private CaseUserAssignmentHandler caseUserAssignmentHandler;

    @MockBean
    private CoreCaseUserService coreCaseUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAssignCaseToApplicantSolicitorOneAndRemoveCreator() {
        CaseData caseData = new CaseDataBuilder().atStateClaimDraft()
            .caseReference(CaseDataBuilder.CASE_ID)
            .businessProcess(BusinessProcess.builder().status(BusinessProcessStatus.READY).build())
            .applicant1OrganisationPolicy(OrganisationPolicy.builder()
                                              .organisation(Organisation.builder().organisationID("OrgId1").build())
                                              .build())
            .respondent1OrganisationPolicy(OrganisationPolicy.builder()
                                               .organisation(Organisation.builder().organisationID("OrgId2").build())
                                               .build())
            .build();

        Map<String, Object> dataMap = objectMapper.convertValue(caseData, new TypeReference<>() {
        });
        CallbackParams params = callbackParamsOf(dataMap, CallbackType.ABOUT_TO_SUBMIT);

        caseUserAssignmentHandler.handle(params);

        verify(coreCaseUserService).assignCase(
            caseData.getCcdCaseReference().toString(),
            caseData.getSubmitterId(),
            "OrgId1",
            CaseRole.APPLICANTSOLICITORONE
        );

        verify(coreCaseUserService).removeCreatorRoleCaseAssignment(
            caseData.getCcdCaseReference().toString(),
            caseData.getSubmitterId(),
            "OrgId1"
        );
    }
}
