package uk.gov.hmcts.reform.unspec.launchdarkly;

import com.launchdarkly.sdk.LDValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.service.OrganisationService;
import uk.gov.hmcts.reform.unspec.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.launchdarkly.OnBoardingOrganisationControlService.ORG_NOT_REGISTERED;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    OnBoardingOrganisationControlService.class,
    JacksonAutoConfiguration.class
})
class OnBoardingOrganisationControlServiceTest {

    public static final String USER_TOKEN = "bearer:userToken";
    @MockBean
    private FeatureToggleService featureToggleService;
    @MockBean
    private OrganisationService organisationService;
    @MockBean
    private UserService userService;

    @Autowired
    private OnBoardingOrganisationControlService onBoardingOrganisationControlService;

    @BeforeEach
    void setUp() {
        LDValue ldValue = LDValue.buildObject()
            .put("orgIds", LDValue.buildArray().add("0FA7S8S").add("N5AFUXG").build())
            .build();

        when(featureToggleService.jsonValueFeature("registeredFirms")).thenReturn(ldValue);
    }

    @Test
    void shouldNotReturnError_whenUserIsSystemUser() {
        when(userService.getUserInfo(USER_TOKEN))
            .thenReturn(UserInfo.builder().roles(List.of("caseworker-civil-systemupdate")).build());

        assertThat(onBoardingOrganisationControlService.isOrganisationAllowed(USER_TOKEN)).isEmpty();
    }

    @Test
    void shouldNotReturnError_whenOrganisationAllowed() {
        when(organisationService.findOrganisation(USER_TOKEN))
            .thenReturn(Optional.of(Organisation.builder().organisationIdentifier("0FA7S8S").build()));

        when(userService.getUserInfo(USER_TOKEN))
            .thenReturn(UserInfo.builder().roles(List.of("caseworker-civil-solicitor")).build());

        assertThat(onBoardingOrganisationControlService.isOrganisationAllowed(USER_TOKEN)).isEmpty();
    }

    @Test
    void shouldReturnError_whenOrganisationNotAllowed() {
        String firm = "Solicitor tribunal ltd";
        when(organisationService.findOrganisation(USER_TOKEN))
            .thenReturn(Optional.of(Organisation.builder().name(firm).organisationIdentifier("0F99S99").build()));

        when(userService.getUserInfo(USER_TOKEN))
            .thenReturn(UserInfo.builder().roles(List.of("caseworker-civil-solicitor")).build());

        assertThat(onBoardingOrganisationControlService.isOrganisationAllowed(USER_TOKEN))
            .contains(String.format(ORG_NOT_REGISTERED, firm));
    }
}
