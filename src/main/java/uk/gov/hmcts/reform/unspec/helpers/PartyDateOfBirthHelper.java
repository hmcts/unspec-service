package uk.gov.hmcts.reform.unspec.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.validation.DateOfBirthValidator;

import java.util.List;
import java.util.Map;

@Service
public class PartyDateOfBirthHelper {

    private final ObjectMapper mapper;
    private final DateOfBirthValidator dateOfBirthValidator;

    private PartyDateOfBirthHelper(ObjectMapper mapper, DateOfBirthValidator dateOfBirthValidator) {
        this.mapper = mapper;
        this.dateOfBirthValidator = dateOfBirthValidator;
    }

    public List<String> validateDateOfBirth(CallbackParams params, String partyFieldId) {
        Map<String, Object> data = params.getRequest().getCaseDetails().getData();
        Party party = mapper.convertValue(data.get(partyFieldId), Party.class);
        return dateOfBirthValidator.validate(party);
    }
}
