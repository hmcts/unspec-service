package uk.gov.hmcts.reform.unspec.launchdarkly;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.service.OrganisationService;

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

    public List<String> validateOrganisation(String userBearer) {
        List<String> errors = new ArrayList<>();

        Optional<Organisation> userOrganisation = organisationService.findOrganisation(userBearer);

        Boolean organisationOnboarded = userOrganisation
            .map(userOrg -> featureToggleService.isOrganisationOnboarded(userOrg.getOrganisationIdentifier()))
            .orElse(false);

        if (!organisationOnboarded) {
            errors.add(String.format(
                ORG_NOT_REGISTERED,
                userOrganisation.map(Organisation::getName).orElse("UnRegistered")
            ));
        }

        return errors;
    }

}
