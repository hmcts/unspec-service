package uk.gov.hmcts.reform.unspec.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.enums.ServiceMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static uk.gov.hmcts.reform.unspec.enums.ServiceMethod.EMAIL;
import static uk.gov.hmcts.reform.unspec.enums.ServiceMethod.FAX;

@Service
@RequiredArgsConstructor
public class DeadlinesCalculator {

    private static final LocalTime MID_NIGHT = LocalTime.of(23, 59, 59);
    private static final LocalTime CLOSE_OFFICE = LocalTime.of(16, 00, 00);

    private final WorkingDayIndicator workingDayIndicator;

    public LocalDate calculateDeemedDateOfService(
        @NonNull LocalDateTime dateOfService,
        @NonNull ServiceMethod serviceMethod
    ) {
        if ((serviceMethod == FAX || serviceMethod == EMAIL) && dateOfService.getHour() >= 16) {
            return dateOfService.toLocalDate().plusDays(1);
        }
        return dateOfService.toLocalDate().plusDays(serviceMethod.getDays());
    }

    public LocalDate calculateDeemedDateOfService(
        @NonNull LocalDate dateOfService,
        @NonNull ServiceMethod serviceMethod
    ) {
        return calculateDeemedDateOfService(dateOfService.atStartOfDay(), serviceMethod);
    }

    public LocalDateTime calculateDefendantResponseDeadline(@NonNull LocalDate deemedDateOfService) {
        LocalDate responseDeadline = deemedDateOfService.plusDays(14);
        return calculateFirstWorkingDay(responseDeadline).atTime(CLOSE_OFFICE);
    }

    public LocalDateTime calculateConfirmationOfServiceDeadline(@NonNull LocalDate issueDate) {
        LocalDate confirmationOfService = issueDate.plusMonths(4);
        return calculateFirstWorkingDay(confirmationOfService).atTime(MID_NIGHT);
    }

    public LocalDate calculateFirstWorkingDay(LocalDate date) {
        while (!workingDayIndicator.isWorkingDay(date)) {
            date = date.plusDays(1);
        }

        return date;
    }
}
