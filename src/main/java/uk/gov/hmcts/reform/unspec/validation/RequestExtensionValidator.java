package uk.gov.hmcts.reform.unspec.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class RequestExtensionValidator {

    private final ObjectMapper mapper;

    public List<String> validateProposedDeadline(CaseDetails caseDetails) {
        List<String> errors = new ArrayList<>();
        LocalDate proposedDeadline = mapper.convertValue(
            caseDetails.getData().get("extensionProposedDeadline"),
            LocalDate.class
        );

        if (!proposedDeadline.isAfter(now())) {
            errors.add("CONTENT TBC: The proposed deadline must be a future date.");
        }

        LocalDateTime responseDeadline = mapper.convertValue(
            caseDetails.getData().get("responseDeadline"),
            LocalDateTime.class
        );
        if (proposedDeadline.isBefore(responseDeadline.toLocalDate())) {
            errors.add("CONTENT TBC: The proposed deadline can't be before the current response deadline.");
        }
        if (proposedDeadline.isAfter(responseDeadline.toLocalDate().plusDays(28))) {
            errors.add("CONTENT TBC: The proposed deadline can't be later than 28 days after the current deadline.");
        }
        return errors;
    }

    public List<String> validateAlreadyRequested(CaseDetails caseDetails) {
        if (isExtensionAlreadyRequested(caseDetails)) {
            return ImmutableList.of("CONTENT TBC: A request for extension can only be requested once.");
        }
        return emptyList();
    }

    private boolean isExtensionAlreadyRequested(CaseDetails caseDetailsBefore) {
        return caseDetailsBefore.getData().get("extensionProposedDeadline") != null;
    }
}
