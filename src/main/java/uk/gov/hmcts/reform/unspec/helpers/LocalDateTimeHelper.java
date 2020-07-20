package uk.gov.hmcts.reform.unspec.helpers;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeHelper {

    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    private LocalDateTimeHelper() {
    }

    @SuppressWarnings({"AbbreviationAsWordInName"})
    public static LocalDateTime nowInUTC() {
        return LocalDateTime.now(UTC_ZONE);
    }
}
