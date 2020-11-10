package uk.gov.hmcts.reform.unspec.model.robotics;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LitigiousParty {

    private String litigiousPartyType;
    private String litigiousPartyName;
    @JsonUnwrapped(prefix = "litigiousParty")
    private RoboticsAddress litigiousPartyAddresses;
    private String litigiousPartyContactDX;
    private String litigiousPartyContactTelephoneNumber;
    private String litigiousPartyContactFaxNumber;
    private String litigiousPartyContactEmailAddress;
    private String litigiousPartyPreferredMethodOfCommunication;
    private String litigiousPartyWelshTranslation;
    private String litigiousPartyReference;
    private String litigiousPartyDateOfService;
    private String litigiousPartyLastDateForService;
    private String litigiousPartyDateOfBirth;
    private String litigiousPartySolicitorPartyOrganisationID;
}
