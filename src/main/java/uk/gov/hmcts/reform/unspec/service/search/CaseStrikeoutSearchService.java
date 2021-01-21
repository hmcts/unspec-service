package uk.gov.hmcts.reform.unspec.service.search;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.search.Query;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@Service
public class CaseStrikeoutSearchService extends ElasticSearchService {

    public CaseStrikeoutSearchService(CoreCaseDataService coreCaseDataService) {
        super(coreCaseDataService);
    }

    //TODO: applicantSolicitor1ClaimStrikeOutDeadlineToRespondentSolicitor1 is not yet set anywhere.
    public Query query(int startIndex) {
        return new Query(
            boolQuery()
                .must(rangeQuery("data.applicantSolicitor1ClaimStrikeOutDeadlineToRespondentSolicitor1").lt("now"))
                .must(beValidState()),
            List.of("reference"),
            startIndex
        );
    }

    public BoolQueryBuilder beValidState() {
        return boolQuery()
            .minimumShouldMatch(1)
            .should(matchQuery("state", "AWAITING_CLAIMANT_INTENTION"));
    }
}
