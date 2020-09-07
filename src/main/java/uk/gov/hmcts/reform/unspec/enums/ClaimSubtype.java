package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.reform.unspec.model.common.dynamiclist.DynamicList;
import uk.gov.hmcts.reform.unspec.model.common.dynamiclist.DynamicListElement;

import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.unspec.enums.ClaimType.PERSONAL_INJURY;

@Getter
@RequiredArgsConstructor
public enum ClaimSubtype {

    ROAD_ACCIDENT(PERSONAL_INJURY, "Road accident"),
    WORK_ACCIDENT(PERSONAL_INJURY, "Work accident"),
    PUBLIC_LIABILITY(PERSONAL_INJURY, "Public liability accident"),
    HOLIDAY_ILLNESS(PERSONAL_INJURY, "Holiday illness"),
    DISEASE_CLAIM(PERSONAL_INJURY, "Disease claim"),
    PERSONAL_INJURY_OTHER(PERSONAL_INJURY, "Personal Injury - other");

    private final ClaimType claimType;
    private final String label;

    public static DynamicList getDynamicList(ClaimType claimType, DynamicList claimSubtypesList) {
        var listItems = Arrays.stream(values())
            .filter(claimSubtype -> claimSubtype.getClaimType() == claimType)
            .map(ClaimSubtype::toDynamicListElement)
            .collect(toList());

        return DynamicList.builder()
            .value(Optional.ofNullable(claimSubtypesList)
                       .map(DynamicList::getValue)
                       .filter(listItems::contains)
                       .orElse(DynamicListElement.builder().build()))
            .listItems(listItems)
            .build();
    }

    private static DynamicListElement toDynamicListElement(ClaimSubtype claimSubtype) {
        return DynamicListElement.builder()
            .code(claimSubtype.name())
            .label(claimSubtype.getLabel())
            .build();
    }
}
