package uk.gov.hmcts.reform.ucmc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceLocation {
    private final Location location;
    private final String other;

    public enum Location {
        RESIDENCE,
        BUSINESS,
        OTHER
    }
}
