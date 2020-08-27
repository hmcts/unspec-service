package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.CourtChoice;

@Data
@Builder
public class CourtLocation {

    private final CourtChoice option;
    private final String courtName;

    public String getCourtName() {
        return option == CourtChoice.OTHER ? courtName : "";
    }
}
