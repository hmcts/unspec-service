package uk.gov.hmcts.reform.ucmc.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.ucmc.model.ClaimValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.formatLocalDateTime;

@WebMvcTest(CreateClaimController.class)
class CreateClaimControllerTest extends BaseControllerTest {

    CreateClaimControllerTest() {
        super("create-claim");
    }

    @Test
    void shouldReturnExpectedErrorInMidEventWhenValuesAreInvalid() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("claimValue", ClaimValue.builder().higherValue(1).lowerValue(10).build());

        AboutToStartOrSubmitCallbackResponse callbackResponse = postMidEvent(data);

        assertThat(callbackResponse.getErrors())
            .containsOnly("CONTENT TBC: Higher value must not be lower than the lower value.");
    }

    @Test
    void shouldReturnNoErrorInMidEventWhenValuesAreValid() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("claimValue", ClaimValue.builder().higherValue(10).lowerValue(1).build());

        AboutToStartOrSubmitCallbackResponse callbackResponse = postMidEvent(data);
        assertThat(callbackResponse.getErrors()).isEmpty();
    }

    @Test
    void shouldReturnNoErrorInMidEventWhenNoValues() throws Exception {
        AboutToStartOrSubmitCallbackResponse callbackResponse = postMidEvent(new HashMap<>());

        assertThat(callbackResponse.getErrors()).isEmpty();
    }

    @Test
    void shouldReturnExpectedSubmittedCallbackResponseObject() throws Exception {
        SubmittedCallbackResponse callbackResponse = postSubmittedEvent(new HashMap<>());

        String documentLink = "https://www.google.com";
        String responsePackLink = "https://formfinder.hmctsformfinder.justice.gov.uk/n9-eng.pdf";
        LocalDateTime serviceDeadline = LocalDate.now().plusDays(112).atTime(23, 59);
        String formattedServiceDeadline = formatLocalDateTime(serviceDeadline, DATE_TIME_AT);

        String body = format(
            "<br />Follow these steps to serve a claim:"
                + "\n* [Download the sealed claim form](%s) (PDF, 123KB)"
                + "\n* Send the form, particulars of claim and [a response pack](%s) (PDF, 266 KB) "
                + "to the defendant by %s"
                + "\n* Confirm service online within 21 days of sending the form, particulars and response pack, before"
                + " 4pm if you're doing this on the due day", documentLink, responsePackLink, formattedServiceDeadline);

        assertThat(callbackResponse).isEqualToComparingFieldByField(
            SubmittedCallbackResponse.builder()
                .confirmationHeader("# Your claim has been issued\n## Claim number: TBC")
                .confirmationBody(body)
                .build());
    }
}
