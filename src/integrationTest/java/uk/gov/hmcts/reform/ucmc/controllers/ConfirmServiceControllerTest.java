package uk.gov.hmcts.reform.ucmc.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(ConfirmServiceController.class)
class ConfirmServiceControllerTest extends BaseControllerTest {

    ConfirmServiceControllerTest() {
        super("confirm-service");
    }

    @Test
    void shouldReturnExpectedAboutToSubmitCallbackResponseObject() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceMethod", "POST");
        data.put("serviceDate", "2099-06-23");

        AboutToStartOrSubmitCallbackResponse response = postAboutToSubmitEvent(data);

        assertThat(response.getData()).containsAllEntriesOf(
            Map.of(
                "deemedDateOfService", "2099-06-25",
                "responseDeadline", "2099-07-09T16:00:00",
                "serviceMethod", "POST",
                "serviceDate", "2099-06-23"
            ));
    }

    @Test
    void shouldReturnExpectedSubmittedCallbackResponseObject() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("deemedDateOfService", "2099-06-25");

        SubmittedCallbackResponse callbackResponse = postSubmittedEvent(data);

        assertThat(callbackResponse).isEqualToComparingFieldByField(
            SubmittedCallbackResponse.builder()
                .confirmationHeader("# You've confirmed service")
                .confirmationBody("<br /> Deemed date of service: 25 June 2099."
                                      + "<br />The defendant must respond before 4:00pm on 9 July 2099."
                                      + "\n\n[Download certificate of service](http://www.google.com) (PDF, 266 KB)")
                .build());
    }
}
