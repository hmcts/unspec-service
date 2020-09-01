package uk.gov.hmcts.reform.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class StartInstanceResponse {

    private final List<Map<String, Object>> links;
    private final String id;
    private final String definitionId;
    private final String businessKey;
    private final String caseInstanceId;
    private final String ended;
    private final String suspended;
    private final String tenantId;

    @JsonCreator
    public StartInstanceResponse(@JsonProperty("links") final List<Map<String, Object>> links,
                                 @JsonProperty("id") final String id,
                                 @JsonProperty("definitionId") final String definitionId,
                                 @JsonProperty("businessKey") final String businessKey,
                                 @JsonProperty("ended") final String ended,
                                 @JsonProperty("suspended") final String suspended,
                                 @JsonProperty("tenantId") final String tenantId,
                                 @JsonProperty("caseInstanceId") final String caseInstanceId) {
        this.links = links;
        this.id = id;
        this.definitionId = definitionId;
        this.businessKey = businessKey;
        this.ended = ended;
        this.suspended = suspended;
        this.tenantId = tenantId;
        this.caseInstanceId = caseInstanceId;
    }
}
