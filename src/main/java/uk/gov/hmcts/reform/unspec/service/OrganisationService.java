package uk.gov.hmcts.reform.unspec.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.prd.client.OrganisationApi;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.model.OrganisationId;
import uk.gov.hmcts.reform.unspec.model.OrganisationPolicy;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.reform.unspec.enums.CaseRole.APPLICANTSOLICITOR1;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganisationService {

    private final OrganisationApi organisationApi;
    private final AuthTokenGenerator authTokenGenerator;

    public Optional<Organisation> findOrganisation(String authToken) {
        try {
            return ofNullable(organisationApi.findUserOrganisation(authToken, authTokenGenerator.generate()));

        } catch (FeignException.NotFound | FeignException.Forbidden ex) {
            log.error("User not registered in MO", ex);
            return Optional.empty();
        }
    }

    public Optional<OrganisationPolicy> findOrganisationPolicy(String authToken) {
        return findOrganisation(authToken)
            .map(org -> OrganisationPolicy.builder()
                .organisation(OrganisationId.builder()
                                  .id(org.getOrganisationIdentifier())
                                  .build())
                .orgPolicyCaseAssignedRole(APPLICANTSOLICITOR1.getFormattedName())
                .build());
    }
}
