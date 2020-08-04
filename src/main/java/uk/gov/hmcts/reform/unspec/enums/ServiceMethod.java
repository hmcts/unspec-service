package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceMethod {
    POST(2),
    DOCUMENT_EXCHANGE(2),
    FAX(0),
    EMAIL(0),
    OTHER(2);

    private final int days;
}
