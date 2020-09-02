package uk.gov.hmcts.reform.unspec.stateflow.model;

import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.function.Predicate;

public class Transition {

    private String sourceState;

    private String targetState;

    private Predicate<CaseDetails> condition;

    public Transition(String sourceState, String targetState) {
        this.sourceState = sourceState;
        this.targetState = targetState;
    }

    public Transition(String sourceState, String targetState, Predicate<CaseDetails> condition) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.condition = condition;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getTargetState() {
        return targetState;
    }

    public void setTargetState(String targetState) {
        this.targetState = targetState;
    }

    public Predicate<CaseDetails> getCondition() {
        return condition;
    }

    public void setCondition(Predicate<CaseDetails> condition) {
        this.condition = condition;
    }
}
