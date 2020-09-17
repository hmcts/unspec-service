package uk.gov.hmcts.reform.unspec.service;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.config.SystemUpdateUserConfiguration;
import uk.gov.hmcts.reform.unspec.model.search.Query;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.CaseDefinitionConstants.CASE_TYPE;
import static uk.gov.hmcts.reform.unspec.CaseDefinitionConstants.JURISDICTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreCaseDataService {

    private final IdamClient idamClient;
    private final CoreCaseDataApi coreCaseDataApi;
    private final SystemUpdateUserConfiguration userConfig;
    private final AuthTokenGenerator authTokenGenerator;

    public void triggerEvent(Long caseId, CaseEvent eventName) {
        triggerEvent(caseId, eventName, Map.of());
    }

    public void triggerEvent(Long caseId, CaseEvent eventName, Map<String, Object> data) {
        String userToken = idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());
        String systemUpdateUserId = idamClient.getUserInfo(userToken).getUid();

        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
            userToken,
            authTokenGenerator.generate(),
            systemUpdateUserId,
            JURISDICTION,
            CASE_TYPE,
            caseId.toString(),
            eventName.getValue()
        );

        coreCaseDataApi.submitEventForCaseWorker(
            userToken,
            authTokenGenerator.generate(),
            systemUpdateUserId,
            JURISDICTION,
            CASE_TYPE,
            caseId.toString(),
            true,
            caseDataContentFromStartEventResponse(startEventResponse, data)
        );
    }

    public SearchResult searchCases(Query query) {
        String userToken = idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());

        return coreCaseDataApi.searchCases(userToken, authTokenGenerator.generate(), CASE_TYPE, query.toString());
    }

    private CaseDataContent caseDataContentFromStartEventResponse(
        StartEventResponse startEventResponse, Map<String, Object> data) {
        var payload = new HashMap<>(startEventResponse.getCaseDetails().getData());
        payload.putAll(data);

        return CaseDataContent.builder()
            .eventToken(startEventResponse.getToken())
            .event(Event.builder()
                       .id(startEventResponse.getEventId())
                       .build())
            .data(payload)
            .build();
    }
}
