package uk.gov.hmcts.reform.unspec.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ClaimValue;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.Document;
import uk.gov.hmcts.reform.unspec.service.docmosis.sealedclaim.SealedClaimFormGenerator;
import uk.gov.hmcts.reform.unspec.utils.ResourceReader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.enums.AllocatedTrack.SMALL_CLAIM;
import static uk.gov.hmcts.reform.unspec.enums.ClaimType.PERSONAL_INJURY_WORK;
import static uk.gov.hmcts.reform.unspec.handler.CreateClaimCallbackHandler.CONFIRMATION_SUMMARY;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;
import static uk.gov.hmcts.reform.unspec.model.documents.DocumentType.SEALED_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.docmosis.DocmosisTemplates.N1;
import static uk.gov.hmcts.reform.unspec.service.documentmanagement.DocumentManagementService.UNSPEC;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    CreateClaimCallbackHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
class CreateClaimCallbackHandlerTest extends BaseCallbackHandlerTest {
    public static final String REFERENCE_NUMBER = "000LR095";
    @MockBean
    private SealedClaimFormGenerator sealedClaimFormGenerator;

    @Autowired
    private CreateClaimCallbackHandler handler;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${unspecified.response-pack-url}")
    private String responsePackLink;

    @BeforeEach
    public void setup() {
        when(sealedClaimFormGenerator.generate(any(CaseData.class), anyString())).thenReturn(getCaseDocument());
    }

    @Nested
    class MidEvent {
        @Test
        void shouldReturnExpectedErrorInMidEvent_whenValuesAreInvalid() {
            Map<String, Object> data = new HashMap<>();
            data.put("claimValue", ClaimValue.builder().higherValue(1).lowerValue(10).build());

            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getErrors())
                .containsOnly("CONTENT TBC: Higher value must not be lower than the lower value.");
        }

        @Test
        void shouldReturnNoErrorInMidEvent_whenValuesAreValid() {
            Map<String, Object> data = new HashMap<>();
            data.put("claimValue", ClaimValue.builder().higherValue(10).lowerValue(1).build());
            data.put("claimType", PERSONAL_INJURY_WORK);
            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getData())
                .isEqualTo(
                    Map.of(
                        "claimValue", ClaimValue.builder().higherValue(10).lowerValue(1).build(),
                        "ccdCaseReference", CASE_ID,
                        "claimType", PERSONAL_INJURY_WORK,
                        "allocatedTrack", SMALL_CLAIM
                    ));
        }
    }

    @Nested
    class AboutToSubmit {

        @Test
        void shouldAddSystemGeneratedDocuments() throws JsonProcessingException {
            CallbackParams params = callbackParamsOf(getCaseData(), CallbackType.ABOUT_TO_SUBMIT);

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            CaseData caseData = objectMapper.convertValue(response.getData(), CaseData.class);
            assertThat(caseData.getSystemGeneratedCaseDocuments()).isNotEmpty()
                .contains(Element.<CaseDocument>builder().value(getCaseDocument()).build());
        }

        private Map<String, Object> getCaseData() throws JsonProcessingException {
            Map<String, Object> caseData = objectMapper.readValue(
                ResourceReader.readString("case_data.json"),
                new TypeReference<>() {
                }
            );
            caseData.remove("systemGeneratedCaseDocuments");

            return caseData;
        }
    }

    @Nested
    class SubmittedEvent {
        @Test
        void shouldReturnExpectedSubmittedCallbackResponseObject() {
            Map<String, Object> data = new HashMap<>();
            int documentSize = 125952;
            Element<CaseDocument> documents = Element.<CaseDocument>builder()
                .value(CaseDocument.builder().documentSize(documentSize).documentType(SEALED_CLAIM).build())
                .build();
            data.put("systemGeneratedCaseDocuments", List.of(documents));
            CallbackParams params = callbackParamsOf(data, CallbackType.SUBMITTED);
            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            LocalDateTime serviceDeadline = LocalDate.now().plusDays(112).atTime(23, 59);
            String formattedServiceDeadline = formatLocalDateTime(serviceDeadline, DATE_TIME_AT);

            String body = format(
                CONFIRMATION_SUMMARY,
                format("/cases/case-details/%s#CaseDocuments", CASE_ID),
                documentSize / 1024,
                responsePackLink,
                formattedServiceDeadline
            );

            assertThat(response).isEqualToComparingFieldByField(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# Your claim has been issued\n## Claim number: TBC")
                    .confirmationBody(body)
                    .build());
        }
    }

    private CaseDocument getCaseDocument() {
        String fileName = format(N1.getDocumentTitle(), REFERENCE_NUMBER);

        return CaseDocument.builder()
            .documentLink(Document.builder()
                              .documentFileName(fileName)
                              .documentBinaryUrl(
                                  "http://dm-store:4506/documents/73526424-8434-4b1f-acca-bd33a3f8338f/binary")
                              .documentUrl("http://dm-store:4506/documents/73526424-8434-4b1f-acca-bd33a3f8338f")
                              .build())
            .documentSize(56975)
            .createdDatetime(LocalDateTime.of(2020, 07, 16, 14, 05, 15, 550439))
            .documentType(SEALED_CLAIM)
            .createdBy(UNSPEC)
            .documentName(fileName)
            .build();
    }
}
