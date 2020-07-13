package uk.gov.hmcts.reform.ucmc.handler;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.ucmc.callback.Callback;
import uk.gov.hmcts.reform.ucmc.callback.CallbackHandler;
import uk.gov.hmcts.reform.ucmc.callback.CallbackParams;
import uk.gov.hmcts.reform.ucmc.callback.CallbackType;
import uk.gov.hmcts.reform.ucmc.callback.CaseEvent;
import uk.gov.hmcts.reform.ucmc.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.model.common.Element;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;
import uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim.SealedClaimFormGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static uk.gov.hmcts.reform.ucmc.callback.CallbackParams.Params.BEARER_TOKEN;
import static uk.gov.hmcts.reform.ucmc.callback.CaseEvent.CREATE_CASE;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.formatLocalDateTime;

@Service
public class CreateClaimCallbackHandler extends CallbackHandler {
    private static final List<CaseEvent> EVENTS = Collections.singletonList(CREATE_CASE);

    private final SealedClaimFormGenerator sealedClaimFormGenerator;
    private final CaseDetailsConverter caseDetailsConverter;

    public CreateClaimCallbackHandler(CaseDetailsConverter caseDetailsConverter,
                                      SealedClaimFormGenerator sealedClaimFormGenerator) {
        this.caseDetailsConverter = caseDetailsConverter;
        this.sealedClaimFormGenerator = sealedClaimFormGenerator;
    }

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.MID, this::validateClaimValues,
            CallbackType.ABOUT_TO_SUBMIT, this::generateSealedClaim,
            CallbackType.SUBMITTED, this::buildConfirmation
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse validateClaimValues(CallbackParams callbackParams) {
        CaseData caseData = caseDetailsConverter.to(callbackParams.getRequest().getCaseDetails());
        List<String> errors = new ArrayList<>();

        if (caseData.getClaimValue() != null && caseData.getClaimValue().hasLargerLowerValue()) {
            errors.add("CONTENT TBC: Higher value must not be lower than the lower value.");
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .build();
    }

    private CallbackResponse generateSealedClaim(CallbackParams callbackParams) {
        List<String> errors = new ArrayList<>();
        String authorisation = callbackParams.getParams().get(BEARER_TOKEN).toString();

        CaseData caseData = caseDetailsConverter.to(callbackParams.getRequest().getCaseDetails());
        CaseDocument sealedClaim = sealedClaimFormGenerator.generate(caseData, authorisation);
        CaseData updated = addToCaseData(caseData, sealedClaim);

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(caseDetailsConverter.convertToMap(updated))
            .errors(errors)
            .build();
    }

    private CaseData addToCaseData(CaseData caseData, CaseDocument sealedClaim) {
        List<Element<CaseDocument>> caseDocuments = new ArrayList<>();
        caseDocuments.addAll(caseData.getSystemGeneratedCaseDocuments());
        caseDocuments.add(Element.<CaseDocument>builder().value(sealedClaim).build());

        return caseData.toBuilder()
            .systemGeneratedCaseDocuments(caseDocuments)
            .build();
    }

    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        String documentLink = "https://www.google.com";
        String responsePackLink = "https://formfinder.hmctsformfinder.justice.gov.uk/n9-eng.pdf";
        LocalDateTime serviceDeadline = LocalDate.now().plusDays(112).atTime(23, 59);
        String formattedServiceDeadline = formatLocalDateTime(serviceDeadline, DATE_TIME_AT);
        String claimNumber = "TBC";

        String body = format(
            "<br />Follow these steps to serve a claim:"
                + "\n* [Download the sealed claim form](%s) (PDF, 123KB)"
                + "\n* Send the form, particulars of claim and [a response pack](%s) (PDF, 266 KB) "
                + "to the defendant by %s"
                + "\n* Confirm service online within 21 days of sending the form, particulars and response pack, before"
                + " 4pm if you're doing this on the due day", documentLink, responsePackLink, formattedServiceDeadline);

        return SubmittedCallbackResponse.builder()
            .confirmationHeader(format("# Your claim has been issued\n## Claim number: %s", claimNumber))
            .confirmationBody(body)
            .build();
    }
}
