package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceLocation {

    private final Location location;
    private final String other;

    public enum Location {
        RESIDENCE,
        BUSINESS,
        OTHER
    }
}
