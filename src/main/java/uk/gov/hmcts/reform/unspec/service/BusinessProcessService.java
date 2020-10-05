package uk.gov.hmcts.reform.unspec.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.FINISHED;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.READY;

@Service
@RequiredArgsConstructor
public class BusinessProcessService {

    private static final String ERROR_MESSAGE = "Business Process Error";

    private final ObjectMapper mapper;

    public List<String> updateBusinessProcess(Map<String, Object> data, CaseEvent caseEvent) {
        BusinessProcess businessProcess = mapper.convertValue(data.get("businessProcess"), BusinessProcess.class);
        if (hasNoOngoingBusinessProcess(businessProcess)) {
            //TODO: set caseEvent in new event field
            data.put("businessProcess", BusinessProcess.builder().event(caseEvent.name()).status(READY));
            return List.of();
        }
        return List.of(ERROR_MESSAGE);
    }

    //TODO: extract once merged:
    //      https://github.com/hmcts/unspec-service/pull/242/files#diff-4c2ee2ed8f256169482f5b867f57ea2dR53
    private boolean hasNoOngoingBusinessProcess(BusinessProcess businessProcess) {
        return businessProcess == null
            || businessProcess.getStatus() == null
            || businessProcess.getStatus() == FINISHED;
    }
}
