package uk.gov.hmcts.reform.unspec.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.validation.groups.AddressGroup;
import uk.gov.hmcts.reform.unspec.validation.groups.DateOfBirthGroup;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {PartyValidator.class, ValidationAutoConfiguration.class})
class PartyValidatorTest {

    @Autowired
    PartyValidator validator;

    @Test
    void shouldReturnError_whenMissingMandatoryAddressLines() {
        Party party = Party.builder()
            .individualDateOfBirth(LocalDate.now().minusYears(19))
            .primaryAddress(Address.builder().build())
            .build();

        var errors = validator.validate(party, AddressGroup.class, DateOfBirthGroup.class);

        assertThat(errors)
            .containsExactlyInAnyOrder(
                "The address line 1 must not be empty",
                "The address line 2 must not be empty"
            );
    }

    @Test
    void shouldReturnNoError_whenPartyIsValid() {
        Party party = Party.builder()
            .individualDateOfBirth(LocalDate.now().minusYears(19))
            .primaryAddress(Address.builder().addressLine1("line1").addressLine2("line2").build())
            .build();

        var errors = validator.validate(party, AddressGroup.class, DateOfBirthGroup.class);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnNoError_whenDateOfBirthIsNotProvided() {
        Party party = Party.builder()
            .primaryAddress(Address.builder().addressLine1("line1").addressLine2("line2").build())
            .build();

        var errors = validator.validate(party, AddressGroup.class, DateOfBirthGroup.class);

        assertThat(errors).isEmpty();
    }
}
