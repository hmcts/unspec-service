package uk.gov.hmcts.reform.ucmc.service;

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

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

@ExtendWith(SpringExtension.class)
class CaseSearchServiceTest {

    @Captor
    private ArgumentCaptor<String> queryCaptor;

    @Mock
    private CoreCaseDataService coreCaseDataService;

    @InjectMocks
    private CaseSearchService searchService;

    @Test
    void shouldSearchCasesByDateProperty() throws JSONException {
        String property = "data.claimIssuedDate";

        SearchResult expectedSearchResults = SearchResult.builder()
                                                 .total(1)
                                                 .cases(List.of(CaseDetails.builder().id(1L).build()))
                                                 .build();

        when(coreCaseDataService.searchCases(any())).thenReturn(expectedSearchResults);

        List<CaseDetails> casesFound = searchService.getCasesOver112Days();

        assertThat(casesFound).isEqualTo(expectedSearchResults.getCases());

        verify(coreCaseDataService).searchCases(queryCaptor.capture());

        String expectedQuery = format("{\"query\":{\"range\":{\"%s\":{\"lt\":\"now-112d\"}}}, "
                                          + "\"_source\": [\"reference\"]}", property);

        JSONAssert.assertEquals(queryCaptor.getValue(), expectedQuery, STRICT);
    }
}
