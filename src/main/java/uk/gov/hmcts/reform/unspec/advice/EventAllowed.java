package uk.gov.hmcts.reform.unspec.advice;

import uk.gov.hmcts.reform.unspec.callback.CaseEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventAllowed {

    CaseEvent caseEvent();
}
