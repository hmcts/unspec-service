package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.validation.groups.ConfirmServiceDateGroup;
import uk.gov.hmcts.reform.unspec.validation.interfaces.HasServiceDateAfterIssueDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.PastOrPresent;

@Data
@Builder
@HasServiceDateAfterIssueDate(groups = ConfirmServiceDateGroup.class)
public class CaseData {

    private final ClaimValue claimValue;
    private final ServiceMethod serviceMethod;

    @PastOrPresent(message = "The date must not be in the future", groups = ConfirmServiceDateGroup.class)
    private final LocalDate serviceDate;

    @PastOrPresent(message = "The date must not be in the future", groups = ConfirmServiceDateGroup.class)
    private final LocalDateTime serviceDateAndTime;

    private final LocalDate claimIssuedDate;

    private final LocalDate deemedDateOfService;
    private final LocalDateTime responseDeadline;
    private final List<ServedDocuments> servedDocuments;
}
