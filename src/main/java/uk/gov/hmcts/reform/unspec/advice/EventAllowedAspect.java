package uk.gov.hmcts.reform.unspec.advice;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.service.flowstate.FlowStateAllowedEventService;

import java.util.List;

import static java.lang.String.format;

@Aspect
@Component
@RequiredArgsConstructor
public class EventAllowedAspect {

    private final FlowStateAllowedEventService flowStateAllowedEventService;

    @Pointcut("execution(* *(*)) && @annotation(EventAllowed)")
    public void eventAllowedPointCut() {
    }

    @Around("eventAllowedPointCut() && args(callbackParams))")
    public Object checkEventAllowed(
        ProceedingJoinPoint joinPoint,
        CallbackParams callbackParams
    ) throws Throwable {
        final CaseEvent caseEvent = CaseEvent.valueOf(callbackParams.getRequest().getEventId());
        final CaseDetails caseDetails = callbackParams.getRequest().getCaseDetails();
        if (flowStateAllowedEventService.isAllowed(caseDetails, caseEvent)) {
            return joinPoint.proceed();
        } else {
            return AboutToStartOrSubmitCallbackResponse.builder()
                .errors(List.of(format("%s is not allowed on the case", caseEvent)))
                .build();
        }
    }
}
