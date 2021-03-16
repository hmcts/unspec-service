package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalTime.MIDNIGHT;

@Service
@RequiredArgsConstructor
public class DeadlinesCalculator {

    public static final LocalTime END_OF_BUSINESS_DAY = LocalTime.of(16, 0);

    private final WorkingDayIndicator workingDayIndicator;

    public LocalDateTime calculateClaimNotificationDeadline(LocalDate claimIssueDate) {
        LocalDate notificationDeadline = claimIssueDate.plusMonths(4);
        return calculateFirstWorkingDay(notificationDeadline).atTime(MIDNIGHT);
    }

    public LocalDateTime plus14DaysAt4pmDeadline(LocalDate startDate) {
        LocalDate notificationDeadline = startDate.plusDays(14);
        return calculateFirstWorkingDay(notificationDeadline).atTime(END_OF_BUSINESS_DAY);
    }

    public LocalDate calculateFirstWorkingDay(LocalDate date) {
        while (!workingDayIndicator.isWorkingDay(date)) {
            date = date.plusDays(1);
        }
        return date;
    }
}
