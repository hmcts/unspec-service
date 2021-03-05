package uk.gov.hmcts.reform.unspec.launchdarkly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.sdk.LDValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.service.OrganisationService;
import uk.gov.hmcts.reform.unspec.service.UserService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnBoardingOrganisationControlService {

    private final FeatureToggleService featureToggleService;
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public boolean isOrganisationAllowed(String userBearer) {
        boolean isSystemUpdateUser = userService.getUserInfo(userBearer).getRoles().stream()
            .anyMatch(r -> r.equals("caseworker-civil-systemupdate"));

        if (isSystemUpdateUser) {
            return true;
        }

        try {
            Optional<Organisation> userOrganisation = organisationService.findOrganisation(userBearer);
            LDValue ldValue = featureToggleService.jsonValueFeature("registeredFirms");

            OnboardedOrganisation onboardedOrganisation
                = objectMapper.readValue(ldValue.toJsonString(), OnboardedOrganisation.class);

            return userOrganisation.map(org -> hasSameOrganisation(onboardedOrganisation, org)).orElse(false);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("invalid list of registered firms");
        }
        return false;
    }

    private boolean hasSameOrganisation(OnboardedOrganisation onboardedOrganisation, Organisation org) {
        return onboardedOrganisation.getOrgIds().stream()
            .anyMatch(id -> id.equals(org.getOrganisationIdentifier()));
    }

}
