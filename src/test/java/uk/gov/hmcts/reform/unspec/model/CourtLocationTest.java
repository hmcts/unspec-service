package uk.gov.hmcts.reform.unspec.model;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.unspec.enums.CourtChoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CourtLocationTest {

    @Test
    void shouldReturnCourtName_whenOptionIsOther() {
        String courtName = "Kent County Court";
        CourtLocation courtLocation = CourtLocation.builder()
            .option(CourtChoice.OTHER)
            .courtName(courtName)
            .build();

        assertThat(courtLocation.getCourtName()).isEqualTo(courtName);
    }

    //TODO: what values should be set for Birkenhead / Liverpool?
    @Test
    void shouldReturnEmptyString_whenOptionIsNotOther() {
        String courtName = "Kent County Court";
        CourtLocation courtLocation = CourtLocation.builder()
            .option(CourtChoice.BIRKENHEAD)
            .courtName(courtName)
            .build();

        assertThat(courtLocation.getCourtName()).isEqualTo("");
    }
}
