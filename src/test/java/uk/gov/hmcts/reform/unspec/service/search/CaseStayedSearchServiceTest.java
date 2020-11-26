package uk.gov.hmcts.reform.unspec.service.search;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import uk.gov.hmcts.reform.unspec.model.search.Query;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

class CaseStayedSearchServiceTest extends ElasticSearchServiceTest {

    @BeforeEach
    void setup() {
        searchService = new CaseStayedSearchService(coreCaseDataService);
    }

    @Override
    protected Query buildQuery(int fromValue) {
        BoolQueryBuilder query = boolQuery()
            .must(rangeQuery("data.caseStayedDeadline").lt("now"))
            .mustNot(existsQuery("data.deemedServiceDateToRespondentSolicitor1"))
            .must(matchQuery("state", "CREATED"));

        return new Query(query, List.of("reference"), fromValue);
    }
}
