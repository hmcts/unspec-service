package uk.gov.hmcts.reform.unspec.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.launchdarkly.FeatureToggleService.UNSPEC_SERVICE_USER;

@ExtendWith(MockitoExtension.class)
class FeatureToggleServiceTest {

    private static final String FAKE_FEATURE = "fake-feature";

    @Mock
    private LDClientInterface ldClient;

    @Mock
    private LDUser ldUser;

    private FeatureToggleService featureToggleService;

    @BeforeEach
    void setUp() {
        featureToggleService = new FeatureToggleService(ldClient);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectState_whenUserIsProvided(Boolean toggleState) {
        givenToggle(FAKE_FEATURE, toggleState);

        assertThat(featureToggleService.isFeatureEnabled(FAKE_FEATURE, ldUser)).isEqualTo(toggleState);

        verify(ldClient).boolVariation(
            eq(FAKE_FEATURE),
            eq(ldUser),
            eq(false)
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectState_whenDefaultServiceUser(Boolean toggleState) {
        givenToggle(FAKE_FEATURE, toggleState);

        assertThat(featureToggleService.isFeatureEnabled(FAKE_FEATURE)).isEqualTo(toggleState);

        verify(ldClient).boolVariation(
            eq(FAKE_FEATURE),
            eq(UNSPEC_SERVICE_USER),
            eq(false)
        );
    }

    private void givenToggle(String feature, boolean state) {
        when(ldClient.boolVariation(eq(feature), any(LDUser.class), anyBoolean()))
            .thenReturn(state);
    }
}
