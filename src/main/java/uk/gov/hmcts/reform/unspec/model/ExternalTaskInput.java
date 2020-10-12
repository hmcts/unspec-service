package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;

@Data
@Builder
public class ExternalTaskInput {

    String caseId;
    CaseEvent caseEvent;
}
