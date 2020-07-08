package uk.gov.hmcts.reform.ucmc.controllers;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.ucmc.callback.CaseEvent;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.ucmc.callback.CallbackType.ABOUT_TO_START;

public class CallbackControllerTest extends BaseIntegrationTest {

    private static final String CALLBACK_URL = "/cases/callbacks/{callback-type}";
    private static final UserInfo USER_INFO = UserInfo.builder().roles(ImmutableList.of("caseworker-cmc")).build();

    @BeforeEach
    public void setUp() {
        given(userService.getUserInfo(BEARER_TOKEN)).willReturn(USER_INFO);
    }

    @Test
    @SneakyThrows
    public void shouldReturnNotFoundWhenCallbackHandlerIsNotImplemented() {
        CallbackRequest callbackRequest = CallbackRequest.builder()
            .eventId(CaseEvent.CREATE_CASE.getValue())
            .caseDetails(CaseDetails.builder().build())
            .build();

        doPost(BEARER_TOKEN, callbackRequest, CALLBACK_URL, ABOUT_TO_START.getValue())
            .andExpect(status().isNotFound());
    }
}
