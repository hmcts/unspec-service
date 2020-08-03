package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.PartyType;

import java.time.LocalDate;

@Data
@Builder
public class Party {

    private final PartyType type;
    private final LocalDate individualDateOfBirth;
    private final LocalDate soleTraderDateOfBirth;
}
