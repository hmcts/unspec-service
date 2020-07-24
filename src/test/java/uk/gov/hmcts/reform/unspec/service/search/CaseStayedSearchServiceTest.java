package uk.gov.hmcts.reform.unspec.service.search;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.unspec.model.search.Query;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CaseStayedSearchServiceTest {

    public static final SearchResult EXPECTED_SEARCH_RESULTS = SearchResult.builder()
        .total(1)
        .cases(List.of(CaseDetails.builder().id(1L).build()))
        .build();

    @Captor
    private ArgumentCaptor<Query> queryCaptor;

    @Mock
    private CoreCaseDataService coreCaseDataService;

    @InjectMocks
    private CaseStayedSearchService searchService;

    @Test
    void shouldGetCases_WhenSearchingCasesByDateProperty() {
        when(coreCaseDataService.searchCases(any())).thenReturn(EXPECTED_SEARCH_RESULTS);

        assertThat(searchService.getCases()).isEqualTo(EXPECTED_SEARCH_RESULTS.getCases());
        verify(coreCaseDataService).searchCases(queryCaptor.capture());
        assertThat(queryCaptor.getValue()).isEqualToComparingFieldByField(buildQuery(0));
    }

    @Test
    void shouldCallGetCasesMultipleTimes_WhenSearchingForMoreCases() {
        SearchResult searchResult = SearchResult.builder()
            .total(2)
            .cases(List.of(CaseDetails.builder().id(1L).build()))
            .build();

        when(coreCaseDataService.searchCases(any())).thenReturn(searchResult);

        assertThat(searchService.getCases()).hasSize(2);
        verify(coreCaseDataService, times(2)).searchCases(queryCaptor.capture());

        List<Query> capturedQueries = queryCaptor.getAllValues();
        assertThat(capturedQueries.get(0)).isEqualToComparingFieldByField(buildQuery(0));
        assertThat(capturedQueries.get(1)).isEqualToComparingFieldByField(buildQuery(10));
    }

    private Query buildQuery(int fromValue) {
        return new Query(
            QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("data.claimIssuedDate").lt("now-112d"))
                .must(QueryBuilders.matchQuery("state", "CREATED")),
            List.of("reference"),
            fromValue
        );
    }
}
