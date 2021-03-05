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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
    void shouldReturnTrue_whenUserIsSystemUser() {
        when(userService.getUserInfo(USER_TOKEN))
            .thenReturn(UserInfo.builder().roles(List.of("caseworker-civil-systemupdate")).build());

        assertTrue(onBoardingOrganisationControlService.isOrganisationAllowed(USER_TOKEN));
    }

    @Test
    void shouldReturnTrue_whenOrganisationAllowed() {
        when(organisationService.findOrganisation(USER_TOKEN))
            .thenReturn(Optional.of(Organisation.builder().organisationIdentifier("0FA7S8S").build()));

        when(userService.getUserInfo(USER_TOKEN))
            .thenReturn(UserInfo.builder().roles(List.of("caseworker-civil-solicitor")).build());

        assertTrue(onBoardingOrganisationControlService.isOrganisationAllowed(USER_TOKEN));
    }

    @Test
    void shouldReturnFalse_whenOrganisationNotAllowed() {
        when(organisationService.findOrganisation(USER_TOKEN))
            .thenReturn(Optional.of(Organisation.builder().organisationIdentifier("0F99S99").build()));

        when(userService.getUserInfo(USER_TOKEN))
            .thenReturn(UserInfo.builder().roles(List.of("caseworker-civil-solicitor")).build());

        assertFalse(onBoardingOrganisationControlService.isOrganisationAllowed(USER_TOKEN));
    }
}
