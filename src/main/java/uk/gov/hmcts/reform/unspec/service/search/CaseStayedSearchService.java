package uk.gov.hmcts.reform.unspec.service.search;

import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.List;

import static java.util.Map.of;

@Service
public class CaseStayedSearchService extends ElasticSearchService {

    public CaseStayedSearchService(CoreCaseDataService coreCaseDataService) {
        super(coreCaseDataService);
    }

    //TODO: update case data to store claimIssuedDate + 4 months. ES query can then be done on this value using now
    public String query(int startIndex) {
        return new JSONObject(
            of("query",
                of("bool",
                    of("must", List.of(
                           of("range", of("data.claimIssuedDate", of("lt", "now-112d"))),
                           of("match", of("state", "CREATED"))
                       ))
                ),
               "_source", List.of("reference"),
               "from", startIndex
            )).toString();
    }
}
