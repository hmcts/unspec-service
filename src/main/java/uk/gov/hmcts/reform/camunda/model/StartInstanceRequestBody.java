package uk.gov.hmcts.reform.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StartInstanceRequestBody {

    private final Map<String, Object> variables;

    @JsonCreator
    StartInstanceRequestBody(@JsonProperty("variables")  Map<String, Object> variables) {
        this.variables = variables;
    }
}
