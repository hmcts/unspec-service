package uk.gov.hmcts.reform.unspec.validation.interfaces;

import uk.gov.hmcts.reform.unspec.validation.HasServiceDateTheSameAsOrAfterSealedClaimGenerationDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HasServiceDateTheSameAsOrAfterSealedClaimGenerationDateValidator.class)
public @interface HasServiceDateTheSameAsOrAfterSealedClaimGenerationDate {
    String message() default "TBC: The date must not be before claim document is issued";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
