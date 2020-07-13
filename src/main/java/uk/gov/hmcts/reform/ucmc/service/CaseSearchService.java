package uk.gov.hmcts.reform.ucmc.service;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Map.of;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CaseSearchService {
    private final CoreCaseDataService coreCaseDataService;

    public static final String CASE_TYPE = "UNSPECIFIED_CLAIMS";

    public List<CaseDetails> searchCasesOver112Days() {
        return coreCaseDataService.searchCases(CASE_TYPE, dateQuery());
    }

    private String dateQuery() {
        final String CLAIM_ISSUED_DATE_PROPERTY = "data.claimIssuedDate";

        final Map<String, Object> dayRange = of("gte", LocalDateTime.now().plusDays(112));

        return new JSONObject(of("query", of("range", of(CLAIM_ISSUED_DATE_PROPERTY, dayRange)))).toString();
    }
}
