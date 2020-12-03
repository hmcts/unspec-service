package uk.gov.hmcts.reform.unspec;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;

@Data
@Builder
public class RpaContract {

    private RoboticsCaseData payload;
    private String title;
    private String version;
}
