package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;

import static java.util.Optional.ofNullable;

@Data
@Builder(toBuilder = true)
public class BusinessProcess {

    private String processInstanceId;
    private BusinessProcessStatus status;
    private String activityId;

    @JsonIgnore
    public boolean hasSimilarProcessInstanceId(String processInstanceId) {
        return this.getProcessInstanceId().equals(processInstanceId);
    }

    @JsonIgnore
    public BusinessProcessStatus getStatusOrDefault() {
        return ofNullable(this.getStatus()).orElse(BusinessProcessStatus.READY);
    }

    @JsonIgnore
    public BusinessProcess reset() {
        this.activityId = null;
        this.processInstanceId = null;
        this.status = BusinessProcessStatus.FINISHED;

        return this;
    }
}
