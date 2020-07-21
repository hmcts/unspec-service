package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.model.common.Element;

import java.util.List;

@Data
@Builder
public class ServedDocumentFiles {

    private List<Element<DocumentReference>> particularsOfClaim;
    private List<Element<DocumentReference>> medicalReports;
    private List<Element<DocumentReference>> scheduleOfLoss;
    private List<Element<DocumentReference>> certificateOfSuitability;
    private List<Element<DocumentReference>> other;
}
