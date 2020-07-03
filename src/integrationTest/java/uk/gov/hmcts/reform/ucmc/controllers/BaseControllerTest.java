package uk.gov.hmcts.reform.ucmc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class BaseControllerTest {
    static final String USER_AUTH_TOKEN = "Bearer token";
    static final String USER_ID = "1";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private final String eventName;

    BaseControllerTest(String eventName) {
        this.eventName = eventName;
    }

    SubmittedCallbackResponse postSubmittedEvent(CallbackRequest callbackRequest) throws Exception {
        MvcResult response = mockMvc
            .perform(post("/" + eventName + "/submitted")
                         .header("authorization", USER_AUTH_TOKEN)
                         .header("user-id", USER_ID)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(toBytes(callbackRequest)))
            .andExpect(status().isOk())
            .andReturn();

        byte[] responseBody = response.getResponse().getContentAsByteArray();

        return toSubmittedCallbackResponse(responseBody);
    }

    SubmittedCallbackResponse postSubmittedEvent(Map<String, Object> data) throws Exception {
        return postSubmittedEvent(toCallbackRequest(data));
    }

    private SubmittedCallbackResponse toSubmittedCallbackResponse(byte[] responseBody) throws java.io.IOException {
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
