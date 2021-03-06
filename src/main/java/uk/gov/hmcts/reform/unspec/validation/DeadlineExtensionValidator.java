package uk.gov.hmcts.reform.unspec.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static uk.gov.hmcts.reform.unspec.service.DeadlinesCalculator.END_OF_BUSINESS_DAY;

@Service
@RequiredArgsConstructor
public class DeadlineExtensionValidator {

    private final ObjectMapper mapper;

    public List<String> validateProposedDeadline(LocalDate dateToValidate, LocalDateTime responseDeadline) {
        if (!dateToValidate.isAfter(now())) {
            return List.of("The agreed extension date must be a date in the future");
        }

        if (!dateToValidate.isAfter(responseDeadline.toLocalDate())) {
            return List.of("The agreed extension date must be after the current deadline");
        }

        if (LocalDateTime.of(dateToValidate, END_OF_BUSINESS_DAY).isAfter(responseDeadline.plusDays(28))) {
            return List.of("The agreed extension date cannot be more than 28 days after the current deadline");
        }

        return emptyList();
    }

    public List<String> validateProposedDeadline(CaseDetails caseDetails) {
        LocalDate deadlineExtension = mapper.convertValue(
            caseDetails.getData().get("respondentSolicitor1AgreedDeadlineExtension"),
            LocalDate.class
        );

        LocalDateTime responseDeadline = mapper.convertValue(
            caseDetails.getData().get("respondentSolicitor1ResponseDeadline"),
            LocalDateTime.class
        );

        return validateProposedDeadline(deadlineExtension, responseDeadline);
    }
}
