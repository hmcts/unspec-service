package uk.gov.hmcts.reform.unspec.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.unspec.model.search.Query;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class ElasticSearchService {

    private final CoreCaseDataService coreCaseDataService;

    private static final int startIndex = 0;
    private static final int esDefaultSearchLimit = 10;

    public List<CaseDetails> getCases() {
        SearchResult searchResult = coreCaseDataService.searchCases(query(startIndex));
        List<CaseDetails> caseDetails = new ArrayList<>(searchResult.getCases());

        for (int i = esDefaultSearchLimit; caseDetails.size() < searchResult.getTotal(); i = i + esDefaultSearchLimit) {
            SearchResult result = coreCaseDataService.searchCases(query(i));
            caseDetails.addAll(result.getCases());
        }

        return caseDetails;
    }

    abstract Query query(int startIndex);
}
