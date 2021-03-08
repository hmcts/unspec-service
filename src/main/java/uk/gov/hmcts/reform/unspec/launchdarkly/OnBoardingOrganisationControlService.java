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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnBoardingOrganisationControlService {

    public static final String ORG_NOT_REGISTERED = "your organisation is %s, unfortunately that org "
        + "is not part of pilot and therefore cannot use Civil Damages service";
    
    private final FeatureToggleService featureToggleService;
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public List<String> isOrganisationAllowed(String userBearer) {
        List<String> errors = new ArrayList<>();
        boolean isSystemUpdateUser = userService.getUserInfo(userBearer).getRoles().stream()
            .anyMatch(r -> r.equals("caseworker-civil-systemupdate"));

        if (isSystemUpdateUser) {
            return errors;
        }

        Optional<Organisation> userOrganisation = organisationService.findOrganisation(userBearer);

        try {
            LDValue registeredFirms = featureToggleService.jsonValueFeature("registeredFirms");

            OnboardedOrganisation onboardedOrganisation
                = objectMapper.readValue(registeredFirms.toJsonString(), OnboardedOrganisation.class);

            Boolean orgIsInMyHmcts = userOrganisation.map(userOrg -> hasSameOrganisation(
                onboardedOrganisation,
                userOrg
            )).orElse(false);

            if (!orgIsInMyHmcts) {
                errors.add(String.format(
                    ORG_NOT_REGISTERED,
                    userOrganisation.map(Organisation::getName).orElse("UnRegistered")
                ));
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("invalid json list of registered firms in launch darkly config");
        }
        return errors;
    }

    private boolean hasSameOrganisation(OnboardedOrganisation onboardedOrganisation, Organisation org) {
        return onboardedOrganisation.getOrgIds().stream()
            .anyMatch(id -> id.equals(org.getOrganisationIdentifier()));
    }

}
