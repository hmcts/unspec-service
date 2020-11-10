package uk.gov.hmcts.reform.unspec.model.robotics;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Solicitor {

    private String solicitorPartyOrganisationID;
    private String solicitorPartyName;
    @JsonUnwrapped(prefix = "solicitorPartyContact")
    private RoboticsAddress solicitorPartyAddresses;
    private String solicitorPartyContactDX;
    private String solicitorPartyContactTelephoneNumber;
    private String solicitorPartyContactFaxNumber;
    private String solicitorPartyContactEmailAddress;
    private String solicitorPartyPreferredMethodOfCommunication;
    private String solicitorPartyReference;
    private boolean solicitorIsPayee;
}
