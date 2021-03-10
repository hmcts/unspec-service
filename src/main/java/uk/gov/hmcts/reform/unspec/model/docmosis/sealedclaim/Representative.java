package uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.SolicitorOrganisationDetails;

@Data
@Builder(toBuilder = true)
public class Representative {

    private final String organisationName;
    private final String phoneNumber;
    private final String dxAddress;
    private final String emailAddress;
    private final Address serviceAddress;

    public static Representative fromSolicitorOrganisationDetails(SolicitorOrganisationDetails solicitorOrganisationDetails) {
        return Representative.builder()
            .dxAddress(solicitorOrganisationDetails.getDx())
            .organisationName(solicitorOrganisationDetails.getOrganisationName())
            .phoneNumber(solicitorOrganisationDetails.getPhoneNumber())
            .emailAddress(solicitorOrganisationDetails.getEmail())
            .serviceAddress(solicitorOrganisationDetails.getAddress())
            .build();
    }
}
