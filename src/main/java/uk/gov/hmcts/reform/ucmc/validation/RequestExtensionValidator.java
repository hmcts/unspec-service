package uk.gov.hmcts.reform.ucmc.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;

@Service
@RequiredArgsConstructor
public class RequestExtensionValidator {

    private final ObjectMapper mapper;

    public List<String> validate(CallbackRequest callbackRequest) {
        CaseDetails caseDetailsBefore = callbackRequest.getCaseDetailsBefore();
        if (isExtensionAlreadyRequested(caseDetailsBefore)) {
            return ImmutableList.of("A request can only be requested once");
        }
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        LocalDate proposedDeadline = mapper.convertValue(
            caseDetails.getData().get("extensionProposedDeadline"),
            LocalDate.class
        );

        if (proposedDeadline.isBefore(now())) {
            return ImmutableList.of("The proposed deadline can't be in the past.");
        }
        return Collections.emptyList();
    }

    private boolean isExtensionAlreadyRequested(CaseDetails caseDetailsBefore) {
        return caseDetailsBefore.getData().get("extensionProposedDeadline") != null;
    }
}
