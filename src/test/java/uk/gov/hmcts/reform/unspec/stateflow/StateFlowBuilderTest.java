package uk.gov.hmcts.reform.unspec.stateflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.stateflow.model.State;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

enum FlowState {
    STATE_1,
    STATE_2,
    STATE_3
}

enum SubflowState {
    STATE_1,
    STATE_2
}

class StateFlowBuilderTest {

    @Nested
    class IllegalArgumentExcpt {

        @Test
        void shouldThrowIllegalArgumentException_WhenFlowNameIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow(null);
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenFlowNameIsEmpty() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow("");
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenSubflowNameIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<SubflowState>subflow(null, new StateFlowContext());
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenSubflowNameIsEmpty() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<SubflowState>subflow("", new StateFlowContext());
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenStateFlowContextIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<SubflowState>subflow("SUBFLOW", null);
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenInitialIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow("FLOW")
                    .initial(null);
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenTransitionToIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow("FLOW")
                    .initial(FlowState.STATE_1)
                    .transitionTo(null);
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenStateIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow("FLOW")
                    .initial(FlowState.STATE_1)
                    .transitionTo(FlowState.STATE_1)
                    .state(null);
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenOnlyIfIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow("FLOW")
                    .initial(FlowState.STATE_1)
                    .transitionTo(FlowState.STATE_1).onlyIf(null);
            });
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenSubflowIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                StateFlowBuilder.<FlowState>flow("FLOW")
                    .initial(FlowState.STATE_1)
                    .subflow(null);
            });
        }
    }

    @Nested
    class Build {

        @Test
        void shouldBuildStateFlow_WhenTransitionIsImplicit() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2)
                .state(FlowState.STATE_2)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_2");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionHasTrueCondition() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> true)
                .state(FlowState.STATE_2)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_2");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionHasFalseCondition() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> false)
                .state(FlowState.STATE_2)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionsAreMutuallyExclusive() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> false)
                .transitionTo(FlowState.STATE_3).onlyIf(caseDetails -> true)
                .state(FlowState.STATE_2)
                .state(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_3");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionsAreMutuallyExclusiveIncludingImplicitTransitions() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> false)
                .transitionTo(FlowState.STATE_3)
                .state(FlowState.STATE_2)
                .state(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_3");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionToMultipleStates() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> true)
                .state(FlowState.STATE_2)
                .transitionTo(FlowState.STATE_3)
                .state(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_2", "FLOW.STATE_3");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionToUndefinedState() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> true)
                .state(FlowState.STATE_2)
                .transitionTo(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_2");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenInitialStateHasSubflow() {
            Consumer<StateFlowContext> subflow = stateFlowContext ->
                StateFlowBuilder.<SubflowState>subflow("SUBFLOW", stateFlowContext)
                    .transitionTo(SubflowState.STATE_1)
                    .state(SubflowState.STATE_1)
                    .transitionTo(SubflowState.STATE_2)
                    .state(SubflowState.STATE_2);

            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .subflow(subflow)
                .state(FlowState.STATE_2)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "SUBFLOW.STATE_1", "SUBFLOW.STATE_2");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlow_WhenTransitionHasSubflow() {
            Consumer<StateFlowContext> subflow = stateFlowContext ->
                StateFlowBuilder.<SubflowState>subflow("SUBFLOW", stateFlowContext)
                    .transitionTo(SubflowState.STATE_1)
                    .state(SubflowState.STATE_1)
                    .transitionTo(SubflowState.STATE_2)
                    .state(SubflowState.STATE_2);

            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2)
                .state(FlowState.STATE_2)
                .subflow(subflow)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates(
                "FLOW.STATE_1",
                "FLOW.STATE_2",
                "SUBFLOW.STATE_1",
                "SUBFLOW.STATE_2"
            );
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isFalse();
        }

        @Test
        void shouldBuildStateFlowButSetStateMachineError_WhenConditionsOnTransitionsAreNotMutuallyExclusive() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> true)
                .transitionTo(FlowState.STATE_3).onlyIf(caseDetails -> true)
                .state(FlowState.STATE_2)
                .state(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_3");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isTrue();
        }

        @Test
        void shouldBuildStateFlowButSetStateMachineError_WhenMoreThanOneTransitionsAreImplicit() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2)
                .transitionTo(FlowState.STATE_3)
                .state(FlowState.STATE_2)
                .state(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_3");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isTrue();
        }

        @Test
        void shouldBuildStateFlowButSetStateMachineError_WhenImplicitTransitionAndConditionalTransitionAreNotMutuallyExclusive() {
            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(caseDetails -> true)
                .transitionTo(FlowState.STATE_3)
                .state(FlowState.STATE_2)
                .state(FlowState.STATE_3)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "FLOW.STATE_3");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isTrue();
        }

        @Test
        void shouldBuildStateFlowWithSubflowButSetStateMachineError_WhenAmbiguousTransitions() {
            Consumer<StateFlowContext> subflow = stateFlowContext ->
                StateFlowBuilder.<SubflowState>subflow("SUBFLOW", stateFlowContext)
                    .transitionTo(SubflowState.STATE_1)
                    .state(SubflowState.STATE_1);

            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2)
                .subflow(subflow)
                .state(FlowState.STATE_2)
                .build();

            StateFlowAssert.assertThat(stateFlow).enteredStates("FLOW.STATE_1", "SUBFLOW.STATE_1");
            assertThat(stateFlow.asStateMachine().hasStateMachineError()).isTrue();
        }
    }

    @Nested
    class Evaluate {

        @Test
        void shouldEvaluateStateAndGetStateHistory() {
            CaseDetails caseDetails = CaseDetails.builder().build();

            Predicate<CaseDetails> firstPredicate = c -> {
                assertThat(c).isSameAs(caseDetails);
                return true;
            };

            Predicate<CaseDetails> secondPredicate = c -> {
                assertThat(c).isSameAs(caseDetails);
                return false;
            };

            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2).onlyIf(firstPredicate)
                .state(FlowState.STATE_2)
                .transitionTo(FlowState.STATE_3).onlyIf(secondPredicate)
                .state(FlowState.STATE_3)
                .build();

            stateFlow.evaluate(caseDetails);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isEqualTo("FLOW.STATE_2");

            assertThat(stateFlow.getStateHistory())
                .hasSize(2)
                .extracting(State::getName)
                .containsExactly("FLOW.STATE_1", "FLOW.STATE_2");
        }

        @Test
        void shouldEvaluateStateAndGetStateHistory_WhenAmbiguousTransitions() {
            CaseDetails caseDetails = CaseDetails.builder().build();

            StateFlow stateFlow = StateFlowBuilder.<FlowState>flow("FLOW")
                .initial(FlowState.STATE_1)
                .transitionTo(FlowState.STATE_2)
                .transitionTo(FlowState.STATE_3)
                .state(FlowState.STATE_2)
                .state(FlowState.STATE_3)
                .build();

            stateFlow.evaluate(caseDetails);

            assertThat(stateFlow.getState())
                .extracting(State::getName)
                .isEqualTo("ERROR");

            assertThat(stateFlow.getStateHistory())
                .hasSize(2)
                .extracting(State::getName)
                .containsExactly("FLOW.STATE_1", "FLOW.STATE_3");
        }
    }
}
