package uk.gov.hmcts.reform.unspec.model;

import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.ServiceLocationType;

@Data
public class ServiceLocation {

    private ServiceLocationType location;
    private String other;
}
