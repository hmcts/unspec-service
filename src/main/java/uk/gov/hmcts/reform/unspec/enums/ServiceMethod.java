package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public enum ServiceMethod {
    POST(2, "First class post"),
    DOCUMENT_EXCHANGE(2, "Document exchange"),
    FAX(0, "Fax"),
    EMAIL(0, "Email"),
    OTHER(2, "Other");

    private final int days;
    private final String label;

    public LocalDate getDeemedDateOfService(LocalDateTime serviceTime) {
        if (this == FAX || this == EMAIL) {
            if (serviceTime.toLocalTime().isAfter(LocalTime.of(15, 59, 59))) {
                return serviceTime.toLocalDate().plusDays(1);
            }
        }

        return serviceTime.toLocalDate().plusDays(this.days);
    }

    public LocalDate getDeemedDateOfService(LocalDate serviceTime) {
        return getDeemedDateOfService(serviceTime.atStartOfDay());
    }
}
