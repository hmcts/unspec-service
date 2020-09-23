package uk.gov.hmcts.reform.unspec.advice;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.service.flowstate.FlowStateAllowedEventService;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class EventAllowedAspect {

    private final FlowStateAllowedEventService flowStateAllowedEventService;

    @Pointcut("execution(* *(..)) && @annotation(eventAllowed)")
    public void eventAllowedPointCut(EventAllowed eventAllowed) {
    }

    @Around("eventAllowedPointCut(eventAllowed) && args(callbackParams))")
    public Object checkEventAllowed(
        ProceedingJoinPoint joinPoint,
        EventAllowed eventAllowed,
        CallbackParams callbackParams
    ) throws Throwable {
        CaseEvent caseEvent = eventAllowed.caseEvent();
        if (flowStateAllowedEventService.isAllowed(callbackParams.getRequest().getCaseDetails(), caseEvent)) {
            return joinPoint.proceed();
        } else {
            return AboutToStartOrSubmitCallbackResponse.builder()
                .errors(List.of(String.format("{} is not allowed on the case", caseEvent)))
                .build();
        }
    }
}
