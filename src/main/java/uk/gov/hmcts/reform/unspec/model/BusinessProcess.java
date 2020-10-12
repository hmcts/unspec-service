package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;

import static java.util.Optional.ofNullable;

@Data
@Builder(toBuilder = true)
public class BusinessProcess {

    private final String processInstanceId;
    private final BusinessProcessStatus status;
    private final String activityId;

    @JsonIgnore
    public boolean hasSimilarProcessInstanceId(String processInstanceId) {
        return this.getProcessInstanceId().equals(processInstanceId);
    }

    @JsonIgnore
    public BusinessProcessStatus getStatusOrDefault() {
        return ofNullable(this.getStatus()).orElse(BusinessProcessStatus.READY);
    }
}
