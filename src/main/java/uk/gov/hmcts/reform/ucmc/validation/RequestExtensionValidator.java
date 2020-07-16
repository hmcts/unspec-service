package uk.gov.hmcts.reform.ucmc.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class RequestExtensionValidator {

    private final ObjectMapper mapper;

    public List<String> validateProposedDeadline(CaseDetails caseDetails) {

        LocalDate proposedDeadline = mapper.convertValue(
            caseDetails.getData().get("extensionProposedDeadline"),
            LocalDate.class
        );

        if (proposedDeadline.isBefore(now())) {
            return ImmutableList.of("The proposed deadline can't be in the past.");
        }
        return emptyList();
    }

    public List<String> validateAlreadyRequested(CaseDetails caseDetails) {
        if (isExtensionAlreadyRequested(caseDetails)) {
            return ImmutableList.of("A request for extension can only be requested once.");
        }
        return emptyList();
    }

    private boolean isExtensionAlreadyRequested(CaseDetails caseDetailsBefore) {
        return caseDetailsBefore.getData().get("extensionProposedDeadline") != null;
    }
}
