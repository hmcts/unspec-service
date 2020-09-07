package uk.gov.hmcts.reform.unspec.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.hmcts.reform.unspec.model.common.dynamiclist.DynamicList;
import uk.gov.hmcts.reform.unspec.model.common.dynamiclist.DynamicListElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.enums.ClaimType.PERSONAL_INJURY;

class ClaimSubtypeTest {

    @Nested
    class GetDynamicList {

        @Test
        void shouldReturnCorrectDynamicList_whenClaimTypeIsPersonalInjury() {
            DynamicList dynamicList = ClaimSubtype.getDynamicList(PERSONAL_INJURY, null);

            assertThat(dynamicList).isEqualTo(DynamicList.builder().listItems(List.of(
                dynamicListElement("ROAD_ACCIDENT", "Road accident"),
                dynamicListElement("WORK_ACCIDENT", "Work accident"),
                dynamicListElement("PUBLIC_LIABILITY", "Public liability accident"),
                dynamicListElement("HOLIDAY_ILLNESS", "Holiday illness"),
                dynamicListElement("DISEASE_CLAIM", "Disease claim"),
                dynamicListElement("PERSONAL_INJURY_OTHER", "Personal Injury - other")
            )).value(DynamicListElement.builder().build()).build());
        }

        @ParameterizedTest()
        @EnumSource(
            value = ClaimType.class,
            names = {"CLINICAL_NEGLIGENCE", "BREACH_OF_CONTRACT", "CONSUMER_CREDIT", "OTHER"})
        void shouldReturnEmptyDynamicList_whenClaimTypeIsNotPersonalInjury(ClaimType claimType) {
            DynamicList dynamicList = ClaimSubtype.getDynamicList(claimType, null);

            assertThat(dynamicList.getListItems()).isEmpty();
        }

        @Test
        void shouldKeepSelectedValue_whenItBelongsToClaimType() {
            DynamicList previouslyPopulatedDynamicList = DynamicList.builder()
                .value(dynamicListElement("WORK_ACCIDENT", "Work accident"))
                .build();
            DynamicList dynamicList = ClaimSubtype.getDynamicList(PERSONAL_INJURY, previouslyPopulatedDynamicList);

            assertThat(dynamicList).isEqualTo(DynamicList.builder().listItems(List.of(
                dynamicListElement("ROAD_ACCIDENT", "Road accident"),
                dynamicListElement("WORK_ACCIDENT", "Work accident"),
                dynamicListElement("PUBLIC_LIABILITY", "Public liability accident"),
                dynamicListElement("HOLIDAY_ILLNESS", "Holiday illness"),
                dynamicListElement("DISEASE_CLAIM", "Disease claim"),
                dynamicListElement("PERSONAL_INJURY_OTHER", "Personal Injury - other")
            )).value(dynamicListElement("WORK_ACCIDENT", "Work accident")).build());
        }

        @ParameterizedTest()
        @EnumSource(
            value = ClaimType.class,
            names = {"CLINICAL_NEGLIGENCE", "BREACH_OF_CONTRACT", "CONSUMER_CREDIT", "OTHER"})
        void shouldRemoveSelectedValue_whenItDoesNotBelongToClaimType(ClaimType claimType) {
            DynamicList previouslyPopulatedDynamicList = DynamicList.builder()
                .value(dynamicListElement("WORK_ACCIDENT", "Work accident"))
                .build();
            DynamicList dynamicList = ClaimSubtype.getDynamicList(claimType, previouslyPopulatedDynamicList);

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
