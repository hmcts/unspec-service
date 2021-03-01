package uk.gov.hmcts.reform.unspec.model.robotics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDetails {

    private String miscText;
    private String responseIntention;
    private String agreedExtensionDate;
}
