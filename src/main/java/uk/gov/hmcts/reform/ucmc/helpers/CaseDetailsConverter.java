package uk.gov.hmcts.reform.ucmc.helpers;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ucmc.model.CaseData;

import java.util.Map;

@Service
public class CaseDetailsConverter {
    private final JsonMapper jsonMapper;

    public CaseDetailsConverter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public CaseData to(CaseDetails caseDetails) {
        Map<String, Object> data = caseDetails.getData();
        data.put("state", caseDetails.getState());
        data.put("id", caseDetails.getId());
        return jsonMapper.fromMap(data, CaseData.class);
    }

    public <T> T to(Object input, Class<T> type) {
        return jsonMapper.convertValue(input, type);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToMap(CaseData caseData) {
        return (Map<String, Object>) jsonMapper.convertValue(caseData, Map.class);
    }
}
