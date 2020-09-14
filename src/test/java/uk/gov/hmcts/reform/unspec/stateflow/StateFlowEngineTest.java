package uk.gov.hmcts.reform.unspec.stateflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.stateflow.model.State;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.CLAIM_ISSUED;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.CLAIM_STAYED;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.DRAFT;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.EXTENSION_REQUESTED;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.EXTENSION_RESPONDED;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.RESPONDED_TO_CLAIM;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.SERVICE_ACKNOWLEDGED;
import static uk.gov.hmcts.reform.unspec.stateflow.StateFlowEngine.FlowState.SERVICE_CONFIRMED;

@SpringBootTest(classes = {
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class,
    StateFlowEngine.class
})
class StateFlowEngineTest {

    @Autowired
    private StateFlowEngine stateFlowEngine;

    @Nested
    class EvaluateStateFlowEngine {

        @Test
        void shouldReturnClaimIssued_whenCaseDataAtStateClaimIssued() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimCreated().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(CLAIM_ISSUED.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(2)
                .extracting(State::getName)
                .containsExactly(DRAFT.fullName(), CLAIM_ISSUED.fullName());
        }

        @Test
        void shouldReturnClaimStayed_whenCaseDataAtStateClaimStayed() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimStayed().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(CLAIM_STAYED.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(3)
                .extracting(State::getName)
                .containsExactly(DRAFT.fullName(), CLAIM_ISSUED.fullName(), CLAIM_STAYED.fullName());
        }

        @Test
        void shouldReturnServiceConfirmed_whenCaseDataAtServiceConfirmed() {
            CaseData caseData = CaseDataBuilder.builder().atStateServiceConfirmed().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(SERVICE_CONFIRMED.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(3)
                .extracting(State::getName)
                .containsExactly(DRAFT.fullName(), CLAIM_ISSUED.fullName(), SERVICE_CONFIRMED.fullName());
        }

        @Test
        void shouldReturnServiceAcknowledge_whenCaseDataAtStateServiceAcknowledge() {
            CaseData caseData = CaseDataBuilder.builder().atStateServiceAcknowledge().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(SERVICE_ACKNOWLEDGED.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(4)
                .extracting(State::getName)
                .containsExactly(
                    DRAFT.fullName(), CLAIM_ISSUED.fullName(),
                    SERVICE_CONFIRMED.fullName(), SERVICE_ACKNOWLEDGED.fullName()
                );
        }

        @Test
        void shouldReturnExtensionRequested_whenCaseDataAtStateExtensionRequested() {
            CaseData caseData = CaseDataBuilder.builder().atStateExtensionRequested().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(EXTENSION_REQUESTED.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(5)
                .extracting(State::getName)
                .containsExactly(
                    DRAFT.fullName(), CLAIM_ISSUED.fullName(),
                    SERVICE_CONFIRMED.fullName(), SERVICE_ACKNOWLEDGED.fullName(),
                    EXTENSION_REQUESTED.fullName()
                );
        }

        @Test
        void shouldReturnExtensionResponded_whenCaseDataAtStateExtensionResponded() {
            CaseData caseData = CaseDataBuilder.builder().atStateExtensionResponded().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(EXTENSION_RESPONDED.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(6)
                .extracting(State::getName)
                .containsExactly(
                    DRAFT.fullName(), CLAIM_ISSUED.fullName(),
                    SERVICE_CONFIRMED.fullName(), SERVICE_ACKNOWLEDGED.fullName(),
                    EXTENSION_REQUESTED.fullName(), EXTENSION_RESPONDED.fullName()
                );
        }

        @Test
        void shouldReturnRespondToClaim_whenCaseDataAtStateRespondedToClaim() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondedToClaim().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(RESPONDED_TO_CLAIM.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(4)
                .extracting(State::getName)
                .containsExactly(
                    DRAFT.fullName(), CLAIM_ISSUED.fullName(),
                    SERVICE_CONFIRMED.fullName(), RESPONDED_TO_CLAIM.fullName()
                );
        }

        @Test
        void shouldReturnClaimantRespond_whenCaseDataAtStateFullDefence() {
            CaseData caseData = CaseDataBuilder.builder().atStateFullDefence().build();

            StateFlow stateFlow = stateFlowEngine.evaluate(caseData);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isNotNull()
                .isEqualTo(FULL_DEFENCE.fullName());
            assertThat(stateFlow.getStateHistory())
                .hasSize(5)
                .extracting(State::getName)
                .containsExactly(
                    DRAFT.fullName(), CLAIM_ISSUED.fullName(),
                    SERVICE_CONFIRMED.fullName(), RESPONDED_TO_CLAIM.fullName(),
                    FULL_DEFENCE.fullName()
                );
        }
    }

    @Nested
    @SuppressWarnings("unchecked")
    class HasTransitionedTo {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateExtensionRequested() {
            CaseData caseData = CaseDataBuilder.builder().atStateExtensionRequested().build();
            ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            CaseDetails caseDetails = CaseDetails.builder()
                .data(mapper.convertValue(caseData, Map.class))
                .state("CREATED")
                .build();
            assertTrue(stateFlowEngine.hasTransitionedTo(caseDetails, EXTENSION_REQUESTED));
        }
    }
}
