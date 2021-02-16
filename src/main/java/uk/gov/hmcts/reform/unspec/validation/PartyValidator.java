package uk.gov.hmcts.reform.unspec.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.model.Party;

import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class PartyValidator {

    private final Validator validator;

    public List<String> validate(Party party, Class<?>... groups) {
        return validator.validate(party, groups).stream()
            .map(ConstraintViolation::getMessage)
            .collect(toList());
    }
}
