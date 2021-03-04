package uk.gov.hmcts.reform.unspec.launchdarkly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.sdk.LDValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.service.OrganisationService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingOrganisationControlService {

    private final FeatureToggleService featureToggleService;
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;

    public boolean isOrganisationAllowed(String userBearer) {
        try {
            Optional<Organisation> userOrganisation = organisationService.findOrganisation(userBearer);
            LDValue ldValue = featureToggleService.jsonValueFeature("registeredFirms");
            OnboardedOrganisation onboardedOrganisation
                = objectMapper.readValue(ldValue.toJsonString(), OnboardedOrganisation.class);

            return userOrganisation
                .map(org -> onboardedOrganisation.getOrgIds().stream()
                    .anyMatch(id -> id.equals(org.getOrganisationIdentifier()))
                )
                .orElse(false);

        } catch (JsonProcessingException jsonProcessingException) {
            log.error("invalid list of registered firms");
        }
        return false;
    }

}
