package uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.SolicitorOrganisationDetails;

import static uk.gov.hmcts.reform.unspec.model.Address.fromContactInformation;

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

    public static Representative fromOrganisation(Organisation organisation) {
        return Representative.builder()
//            .dxAddress("")
            .organisationName(organisation.getName())
//            .phoneNumber("")
//            .emailAddress("")
            .serviceAddress(fromContactInformation(organisation.getContactInformation().get(0)))
            .build();
    }

}
