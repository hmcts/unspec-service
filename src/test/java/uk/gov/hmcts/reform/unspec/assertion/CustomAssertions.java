package uk.gov.hmcts.reform.unspec.assertion;

import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsAddress;

public class CustomAssertions {

    private CustomAssertions() {
        //utility class
    }

    public static RoboticsAddressAssert assertThat(RoboticsAddress roboticsAddress) {
        return new RoboticsAddressAssert(roboticsAddress);
    }
}
