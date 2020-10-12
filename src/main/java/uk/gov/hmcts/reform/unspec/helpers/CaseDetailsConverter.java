package uk.gov.hmcts.reform.unspec.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.enums.CaseState;
import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaseDetailsConverter {

    private static final String DESERIALIZATION_ERROR_MESSAGE = "Failed to deserialize '%s' from JSON";
    private final ObjectMapper objectMapper;

    public CaseDetailsConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CaseData toCaseData(CaseDetails caseDetails) {
        Map<String, Object> data = new HashMap<>(caseDetails.getData());
        data.put("ccdCaseReference", caseDetails.getId());
        if (caseDetails.getState() != null) {
            data.put("ccdState", CaseState.valueOf(caseDetails.getState()));
        }
        return this.fromMap(data, CaseData.class);
    }

    public <T> T fromMap(Map<String, Object> input, Class<T> clazz) {
        return objectMapper.convertValue(input, clazz);
    }

    public Map<String, Object> convertToMap(CaseData caseData) {
        return objectMapper.convertValue(caseData, new TypeReference<>() {
        });
    }
}
