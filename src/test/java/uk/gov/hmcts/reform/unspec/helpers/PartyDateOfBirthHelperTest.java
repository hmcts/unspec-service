package uk.gov.hmcts.reform.unspec.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.validation.DateOfBirthValidator;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyDateOfBirthHelperTest {

    public static final String ERROR = "error";

    @Mock
    DateOfBirthValidator validator;

    @Mock
    ObjectMapper mapper;

    @InjectMocks
    PartyDateOfBirthHelper helper;

    @BeforeEach
    void setup() {
        Party party = Party.builder().build();
        when(mapper.convertValue(any(), eq(Party.class))).thenReturn(party);
        when(validator.validate(party)).thenReturn(List.of(ERROR));
    }

    @Test
    void shouldReturnAListOfErrors_whenValidatorReturnsErrors() {
        CallbackParams params = getCallbackParams();

        assertThat(helper.validateDateOfBirth(params, "")).containsOnly(ERROR);
    }

    private CallbackParams getCallbackParams() {
        return CallbackParams.builder()
            .request(CallbackRequest.builder()
                         .caseDetails(CaseDetails.builder().data(Map.of()).build())
                         .build())
            .build();
    }
}
