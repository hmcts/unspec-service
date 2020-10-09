package uk.gov.hmcts.reform.unspec.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.START_BUSINESS_PROCESS;

@Service
@RequiredArgsConstructor
public class StartBusinessProcessCallbackHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = List.of(START_BUSINESS_PROCESS);
    public static final String BUSINESS_PROCESS = "businessProcess";

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(callbackKey(CallbackType.ABOUT_TO_SUBMIT), this::startBusinessProcess);
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private final CaseDetailsConverter caseDetailsConverter;

    private CallbackResponse startBusinessProcess(CallbackParams callbackParams) {
        CaseData data = caseDetailsConverter.toCaseData(callbackParams.getRequest().getCaseDetails());
        BusinessProcess businessProcess = data.getBusinessProcess();

        switch (getStatus(businessProcess)) {
            case READY:
            case DISPATCHED: {
                businessProcess = businessProcess.toBuilder()
                    .activityId(null)
                    .status(BusinessProcessStatus.STARTED)
                    .build();

                Map<String, Object> output = new HashMap<>(callbackParams.getRequest().getCaseDetails().getData());
                output.put(BUSINESS_PROCESS, businessProcess);

                return AboutToStartOrSubmitCallbackResponse.builder()
                    .data(output)
                    .build();
            }
            default:
                return AboutToStartOrSubmitCallbackResponse.builder()
                    .errors(List.of("Concurrency Error"))
                    .build();

        }
    }

    private BusinessProcessStatus getStatus(BusinessProcess businessProcess) {
        return Optional.ofNullable(businessProcess.getStatus()).orElse(BusinessProcessStatus.READY);
    }
}
