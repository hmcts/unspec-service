package uk.gov.hmcts.reform.unspec.service.search;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.search.Query;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
public class CaseReadySearchService extends ElasticSearchService {

    public CaseReadySearchService(CoreCaseDataService coreCaseDataService) {
        super(coreCaseDataService);
    }

    public Query query(int startIndex) {
        return new Query(
            boolQuery().must(matchQuery("data.businessProcess.status", "READY")),
            List.of(),
            startIndex
        );
    }
}
