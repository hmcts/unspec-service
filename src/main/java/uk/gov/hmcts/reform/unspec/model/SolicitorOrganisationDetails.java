package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.prd.model.Organisation;

@Data
@Builder(toBuilder = true)
public class SolicitorOrganisationDetails {

    private final String email;
    private final String organisationName;
    private final String fax;
    private final String dx;
    private final String phoneNumber;
    private final Address address;

    @JsonIgnore
    public static SolicitorOrganisationDetails fromOrganisation(Organisation organisation) {
        return SolicitorOrganisationDetails.builder()
            .organisationName(organisation.getName())
            .address(Address.fromContactInformation(organisation.getContactInformation().get(0)))
            .build();
    }
}
