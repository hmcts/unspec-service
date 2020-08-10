package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.PartyType;
import uk.gov.hmcts.reform.unspec.validation.groups.DateOfBirthGroup;

import java.time.LocalDate;
import javax.validation.constraints.PastOrPresent;

@Data
@Builder
public class Party {

    private final PartyType type;

    @PastOrPresent(message = "The date entered cannot be in the future", groups = DateOfBirthGroup.class)
    private final LocalDate individualDateOfBirth;

    @PastOrPresent(message = "The date entered cannot be in the future", groups = DateOfBirthGroup.class)
    private final LocalDate soleTraderDateOfBirth;
}
