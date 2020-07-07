package uk.gov.hmcts.reform.ucmc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class BaseControllerTest {
    static final String USER_AUTH_TOKEN = "Bearer token";
    static final String USER_ID = "1";
    public static final String ABOUT_TO_SUBMIT = "about-to-submit";
    public static final String MID = "mid-event";
    public static final String SUBMITTED = "submitted";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private final String eventName;

    BaseControllerTest(String eventName) {
        this.eventName = eventName;
    }

    SubmittedCallbackResponse postSubmittedEvent(Map<String, Object> data) throws Exception {
        return postSubmittedEvent(toCallbackRequest(data));
    }

    SubmittedCallbackResponse postSubmittedEvent(CallbackRequest callbackRequest) throws Exception {
        MvcResult response = getMvcResult(callbackRequest, SUBMITTED);

        byte[] responseBody = response.getResponse().getContentAsByteArray();

        return toSubmittedResponse(responseBody);
    }

    AboutToStartOrSubmitCallbackResponse postMidEvent(CallbackRequest request) throws Exception {
        return postEvent(request, MID);
    }

    AboutToStartOrSubmitCallbackResponse postMidEvent(Map<String, Object> data) throws Exception {
        return postMidEvent(toCallbackRequest(data));
    }

    AboutToStartOrSubmitCallbackResponse postAboutToSubmitEvent(Map<String, Object> data) throws Exception {
        return postAboutToSubmitEvent(toCallbackRequest(data));
    }

    AboutToStartOrSubmitCallbackResponse postAboutToSubmitEvent(CallbackRequest callbackRequest) throws Exception {
        return postEvent(callbackRequest, ABOUT_TO_SUBMIT);
    }

    AboutToStartOrSubmitCallbackResponse postEvent(CallbackRequest callbackRequest, String event) throws Exception {
        MvcResult response = getMvcResult(callbackRequest, event);

        byte[] responseBody = response.getResponse().getContentAsByteArray();

        return toAboutToSubmitResponse(responseBody);
    }

    private MvcResult getMvcResult(CallbackRequest callbackRequest, String callbackType) throws Exception {
        return mockMvc
            .perform(post("/" + eventName + "/" + callbackType)
                         .header("authorization", USER_AUTH_TOKEN)
                         .header("user-id", USER_ID)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(toBytes(callbackRequest)))
            .andExpect(status().isOk())
            .andReturn();
    }

    private AboutToStartOrSubmitCallbackResponse toAboutToSubmitResponse(byte[] body) throws java.io.IOException {
        if (body.length > 0) {
            return mapper.readValue(body, AboutToStartOrSubmitCallbackResponse.class);
        } else {
            return null;
        }
    }

    private SubmittedCallbackResponse toSubmittedResponse(byte[] responseBody) throws java.io.IOException {
        if (responseBody.length > 0) {
            return mapper.readValue(responseBody, SubmittedCallbackResponse.class);
        } else {
            return null;
        }
    }

    private byte[] toBytes(Object o) {
        try {
            return mapper.writeValueAsString(o).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private CallbackRequest toCallbackRequest(Map<String, Object> data) {
        return CallbackRequest.builder()
            .caseDetails(CaseDetails.builder().data(data).build())
            .build();
    }
}
