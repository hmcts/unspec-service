package uk.gov.hmcts.reform.unspec.stateflow.model;

public class State {

    public static String ERROR = "ERROR";

    private String name;

    public static State from(String name) {
        return new State(name);
    }

    public static State error() {
        return new State(ERROR);
    }

    private State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "State{" +
            "name='" + name + '\'' +
            '}';
    }
}
