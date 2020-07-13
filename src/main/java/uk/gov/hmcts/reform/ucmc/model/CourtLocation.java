package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourtLocation {
    private final String preferredCourt;

    //needs class constructor or throws error when mapping due to single field class

    @JsonCreator
    CourtLocation(@JsonProperty("preferredCourt") String preferredCourt) {
        this.preferredCourt = preferredCourt;
    }
}
