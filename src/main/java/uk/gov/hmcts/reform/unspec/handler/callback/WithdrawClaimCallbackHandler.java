package uk.gov.hmcts.reform.unspec.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.validation.groups.ClaimWithdrawalDateGroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import static java.lang.String.format;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.WITHDRAW_CLAIM;

@Service
@RequiredArgsConstructor
public class WithdrawClaimCallbackHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = Collections.singletonList(WITHDRAW_CLAIM);

    private final CaseDetailsConverter caseDetailsConverter;
    private final Validator validator;

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.MID, this::validateWithdrawalDate,
            CallbackType.SUBMITTED, this::buildConfirmation
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    //TODO: should date be after claim issue date??
    private CallbackResponse validateWithdrawalDate(CallbackParams callbackParams) {
        CaseData caseData = caseDetailsConverter.toCaseData(callbackParams.getRequest().getCaseDetails());
        List<String> errors = validator.validate(caseData, ClaimWithdrawalDateGroup.class).stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .build();
    }

    //TODO: not sure if confirmation is required, not currently designed or in ticket
    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        String claimNumber = "TBC";

        return SubmittedCallbackResponse.builder()
            .confirmationHeader(format("# You have withdrawn the claim %n## Claim number: %s", claimNumber))
            .build();
    }
}
