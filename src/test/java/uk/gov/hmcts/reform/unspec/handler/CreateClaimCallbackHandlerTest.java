package uk.gov.hmcts.reform.unspec.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.model.ClaimValue;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;

@SpringBootTest
class CreateClaimCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private CreateClaimCallbackHandler handler;
    @Value("${unspecified.response-pack-url}")
    private String responsePackLink;

    @Test
    void shouldReturnExpectedErrorInMidEventWhenValuesAreInvalid() {
        Map<String, Object> data = new HashMap<>();
        data.put("claimValue", ClaimValue.builder().higherValue("1").lowerValue("10").build());

        CallbackParams params = callbackParamsOf(data, CallbackType.MID);

        AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        assertThat(response.getErrors())
            .containsOnly("CONTENT TBC: Higher value must not be lower than the lower value.");
    }

    @Test
    void shouldReturnNoErrorInMidEventWhenValuesAreValid() {
        Map<String, Object> data = new HashMap<>();
        data.put("claimValue", ClaimValue.builder().higherValue("10").lowerValue("1").build());

        CallbackParams params = callbackParamsOf(data, CallbackType.MID);

        AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    void shouldReturnNoErrorInMidEventWhenNoValues() {
        CallbackParams params = callbackParamsOf(new HashMap<>(), CallbackType.MID);

        AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    void shouldReturnExpectedSubmittedCallbackResponseObject() {
        Map<String, Object> data = new HashMap<>();
        Long caseId = 1594901956117591L;
        Element<CaseDocument> documents = Element.<CaseDocument>builder()
            .value(CaseDocument.builder().size(125952).documentType(DocumentType.SEALED_CLAIM).build())
            .build();
        data.put("systemGeneratedCaseDocuments", List.of(documents));
        data.put("id", caseId);
        CallbackParams params = callbackParamsOf(data, CallbackType.SUBMITTED);
        SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

        LocalDateTime serviceDeadline = LocalDate.now().plusDays(112).atTime(23, 59);
        String formattedServiceDeadline = formatLocalDateTime(serviceDeadline, DATE_TIME_AT);

        String body = format(
            "<br />Follow these steps to serve a claim:"
                + "\n* <a href=\"/cases/case-details/%s#CaseDocuments\" target=\"_blank\">[Download the sealed claim form]</a> (PDF, 123KB)"
                + "\n* Send the form, particulars of claim and "
                + "<a href=\"%s\" target=\"_blank\">a response pack</a> (PDF, 266 KB) to the defendant by %s"
                + "\n* Confirm service online within 21 days of sending the form, particulars and response pack, before"
                + " 4pm if you're doing this on the due day", caseId, responsePackLink, formattedServiceDeadline);

        assertThat(response).isEqualToComparingFieldByField(
            SubmittedCallbackResponse.builder()
                .confirmationHeader("# Your claim has been issued\n## Claim number: TBC")
                .confirmationBody(body)
                .build());
    }
}
