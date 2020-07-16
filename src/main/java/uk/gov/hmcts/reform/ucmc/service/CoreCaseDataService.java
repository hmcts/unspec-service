package uk.gov.hmcts.reform.ucmc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.ucmc.config.SystemUpdateUserConfiguration;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoreCaseDataService {
    private final IdamClient idamClient;
    private final CoreCaseDataApi coreCaseDataApi;
    private final SystemUpdateUserConfiguration userConfig;
    private final AuthTokenGenerator authTokenGenerator;

    public static final String CASE_TYPE = "UNSPECIFIED_CLAIMS";
    public static final String JURISDICTION = "CIVIL";

    public void triggerEvent(Long caseId, String eventName) {
        String userToken = idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());
        String systemUpdateUserId = idamClient.getUserInfo(userToken).getUid();

        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
            userToken,
            authTokenGenerator.generate(),
            systemUpdateUserId,
            JURISDICTION,
            CASE_TYPE,
            caseId.toString(),
            eventName);

        coreCaseDataApi.submitEventForCaseWorker(
            userToken,
            authTokenGenerator.generate(),
            systemUpdateUserId,
            JURISDICTION,
            CASE_TYPE,
            caseId.toString(),
            true,
            caseDataContentFromStartEventResponse(startEventResponse));
    }

    public SearchResult searchCases(String query) {
        String userToken = idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());

        return coreCaseDataApi.searchCases(userToken, authTokenGenerator.generate(), CASE_TYPE, query);
    }

    private CaseDataContent caseDataContentFromStartEventResponse(StartEventResponse startEventResponse) {
        return CaseDataContent.builder()
                   .eventToken(startEventResponse.getToken())
                   .event(Event.builder()
                              .id(startEventResponse.getEventId())
                              .build())
                   .data(startEventResponse.getCaseDetails().getData())
                   .build();
    }
}
