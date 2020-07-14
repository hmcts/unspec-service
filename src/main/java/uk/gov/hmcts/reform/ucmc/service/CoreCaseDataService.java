package uk.gov.hmcts.reform.ucmc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.ucmc.config.SystemUpdateUserConfiguration;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoreCaseDataService {
    private final IdamClient idamClient;
    private final CoreCaseDataApi coreCaseDataApi;
    private final SystemUpdateUserConfiguration userConfig;
    private final AuthTokenGenerator authTokenGenerator;

    public static final String CASE_TYPE = "UNSPECIFIED_CLAIMS";

    public void triggerEvent(String jurisdiction,
                             String caseType,
                             Long caseId,
                             String eventName,
                             Map<String, Object> eventData) {
        String userToken = idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());
        String systemUpdateUserId = idamClient.getUserInfo(userToken).getUid();

        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
            userToken,
            authTokenGenerator.generate(),
            systemUpdateUserId,
            jurisdiction,
            caseType,
            caseId.toString(),
            eventName);

        CaseDataContent caseDataContent = CaseDataContent.builder()
                                              .eventToken(startEventResponse.getToken())
                                              .event(Event.builder()
                                                         .id(startEventResponse.getEventId())
                                                         .build())
                                              .data(eventData)
                                              .build();

        coreCaseDataApi.submitEventForCaseWorker(
            userToken,
            authTokenGenerator.generate(),
            systemUpdateUserId,
            jurisdiction,
            caseType,
            caseId.toString(),
            true,
            caseDataContent);
    }

    public List<CaseDetails> searchCases(String query) {
        String userToken = idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());

        return coreCaseDataApi.searchCases(userToken, authTokenGenerator.generate(), CASE_TYPE, query).getCases();
    }
}
