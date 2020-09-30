package uk.gov.hmcts.reform.unspec.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.DISPATCHED;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.READY;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.STARTED;

@Service
@RequiredArgsConstructor
public class BusinessProcessService {

    private static final List<BusinessProcessStatus> ERROR_STATUSES = List.of(READY, DISPATCHED, STARTED);
    private static final String ERROR_MESSAGE = "Business Process Error";

    private final ObjectMapper mapper;

    public void updateBusinessProcess(Map<String, Object> data, String businessProcessEvent, List<String> errors) {
        BusinessProcess businessProcess = mapper.convertValue(data.get("businessProcess"), BusinessProcess.class);
        if (Optional.ofNullable(businessProcess)
            .map(BusinessProcess::getStatus)
            .map(ERROR_STATUSES::contains)
            .orElse(false)) {
            errors.add(ERROR_MESSAGE);
        } else {
            data.put("businessProcess",
                     BusinessProcess.builder().activityId(businessProcessEvent).status(READY).build());
        }
    }

}
