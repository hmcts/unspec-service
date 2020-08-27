package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourtChoice {
    BIRKENHEAD("BIRKENHEAD"),
    LIVERPOOL("LIVERPOOL"),
    OTHER("OTHER");

    private final String value;
}
