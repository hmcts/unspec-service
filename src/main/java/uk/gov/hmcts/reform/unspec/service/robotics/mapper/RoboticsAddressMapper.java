package uk.gov.hmcts.reform.unspec.service.robotics.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.prd.model.ContactInformation;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsAddress;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsAddresses;

import java.util.List;
import java.util.Optional;

import static io.jsonwebtoken.lang.Collections.isEmpty;
import static java.util.Objects.requireNonNull;

@Component
public class RoboticsAddressMapper {

    public RoboticsAddress toRoboticsAddress(Address address) {
        requireNonNull(address);
        return RoboticsAddress.builder()
            .addressLine1(address.firstNonNull())
            .addressLine2(Optional.ofNullable(address.secondNonNull()).orElse("-"))
            .addressLine3(address.thirdNonNull())
            .addressLine4(address.fourthNonNull())
            .addressLine5(address.fifthNonNull())
            .postCode(address.getPostCode())
            .build();
    }

    public RoboticsAddresses toRoboticsAddresses(Address address) {
        requireNonNull(address);
        return RoboticsAddresses.builder()
            .contactAddress(toRoboticsAddress(address))
            .build();
    }

    public RoboticsAddresses toRoboticsAddresses(List<ContactInformation> contactInformation) {
        if (isEmpty(contactInformation)) {
            return null;
        }

        ContactInformation information = contactInformation.get(0);
        return toRoboticsAddresses(Address.builder()
                                       .addressLine1(information.getAddressLine1())
                                       .addressLine2(information.getAddressLine2())
                                       .addressLine3(information.getAddressLine3())
                                       .postCode(information.getPostCode())
                                       .postTown(information.getTownCity())
                                       .county(information.getCounty())
                                       .country(information.getCountry())
                                       .build());
    }
}
