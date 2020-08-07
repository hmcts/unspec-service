package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceMethodType {
    POST(2, DateOrDateTime.DATE),
    DOCUMENT_EXCHANGE(2, DateOrDateTime.DATE),
    FAX(0, DateOrDateTime.DATE_TIME),
    EMAIL(0, DateOrDateTime.DATE_TIME),
    OTHER(2, DateOrDateTime.DATE_TIME);

    private final int days;
    private final DateOrDateTime dateOrDateTime;

    private enum DateOrDateTime {
        DATE,
        DATE_TIME
    }

    public boolean requiresDateEntry() {
        return this.dateOrDateTime == DateOrDateTime.DATE;
    }
}
