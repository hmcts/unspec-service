package uk.gov.hmcts.reform.unspec.service.search;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

@ExtendWith(SpringExtension.class)
class CaseSearchServiceTest {

    public static final SearchResult EXPECTED_SEARCH_RESULTS = SearchResult.builder()
        .total(1)
        .cases(List.of(CaseDetails.builder().id(1L).build()))
        .build();

    @Captor
    private ArgumentCaptor<String> queryCaptor;

    @Mock
    private CoreCaseDataService coreCaseDataService;

    @InjectMocks
    private CaseStayedSearchService searchService;

    @Test
    void shouldGetCases_WhenSearchingCasesByDateProperty() throws JSONException {
        when(coreCaseDataService.searchCases(any())).thenReturn(EXPECTED_SEARCH_RESULTS);

        assertThat(searchService.getCases()).isEqualTo(EXPECTED_SEARCH_RESULTS.getCases());
        verify(coreCaseDataService).searchCases(queryCaptor.capture());
        JSONAssert.assertEquals(queryCaptor.getValue(), getQuery(0), STRICT);
    }

    @Test
    void shouldCallGetCasesMultipleTimes_WhenSearchingForMoreCases() throws JSONException {
        SearchResult searchResult = SearchResult.builder()
            .total(2)
            .cases(List.of(CaseDetails.builder().id(1L).build()))
            .build();

        when(coreCaseDataService.searchCases(any())).thenReturn(searchResult);

        assertThat(searchService.getCases()).hasSize(2);
        verify(coreCaseDataService, times(2)).searchCases(queryCaptor.capture());

        List<String> capturedStrings = queryCaptor.getAllValues();
        JSONAssert.assertEquals(capturedStrings.get(0), getQuery(0), STRICT);
        JSONAssert.assertEquals(capturedStrings.get(1), getQuery(10), STRICT);
    }

    private String getQuery(int fromValue) {
        return format("{\"query\": "
                          + "{\"bool\": "
                          + "{\"must\": ["
                          + "{\"range\": {\"data.claimIssuedDate\": {\"lt\": \"now-112d\"}}}, "
                          + "{\"match\": {\"state\": \"CREATED\"}}]}},"
                          + "\"_source\": [\"reference\"],"
                          + "\"from\": %d}", fromValue);
    }
}
