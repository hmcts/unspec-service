package uk.gov.hmcts.reform.unspec.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.hmcts.reform.unspec.model.common.dynamiclist.DynamicList;
import uk.gov.hmcts.reform.unspec.model.common.dynamiclist.DynamicListElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClaimSubtypeTest {

    @Nested
    class GetDynamicList {

        @Test
        void shouldReturnCorrectDynamicList_whenClaimTypeIsPersonalInjury() {
            DynamicList dynamicList = ClaimSubtype.getDynamicList(ClaimType.PERSONAL_INJURY);

            assertThat(dynamicList).isEqualTo(DynamicList.builder().listItems(List.of(
                dynamicListElement("ROAD_ACCIDENT", "Road accident"),
                dynamicListElement("WORK_ACCIDENT", "Work accident"),
                dynamicListElement("PUBLIC_LIABILITY", "Public liability accident"),
                dynamicListElement("HOLIDAY_ILLNESS", "Holiday illness"),
                dynamicListElement("DISEASE_CLAIM", "Disease claim"),
                dynamicListElement("PERSONAL_INJURY_OTHER", "Personal Injury - other")
            )).build());
        }

        @ParameterizedTest()
        @EnumSource(
            value = ClaimType.class,
            names = {"CLINICAL_NEGLIGENCE", "BREACH_OF_CONTRACT", "CONSUMER_CREDIT", "OTHER"})
        void shouldReturnEmptyDynamicList_whenClaimTypeIsNotPersonalInjury(ClaimType claimType) {
            DynamicList dynamicList = ClaimSubtype.getDynamicList(claimType);

            assertThat(dynamicList.getListItems()).isEmpty();
        }
    }

    private DynamicListElement dynamicListElement(String code, String label) {
        return DynamicListElement.builder()
            .code(code)
            .label(label)
            .build();
    }
}
