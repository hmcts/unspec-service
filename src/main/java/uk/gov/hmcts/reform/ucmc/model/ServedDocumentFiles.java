package uk.gov.hmcts.reform.ucmc.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.ucmc.model.common.Element;

import java.util.List;

@Data
@Builder
public class ServedDocumentFiles {
    List<Element<DocumentReference>> particularsOfClaim;
    List<Element<DocumentReference>> medicalReports;
    List<Element<DocumentReference>> scheduleOfLoss;
    List<Element<DocumentReference>> certificateOfSuitability;
    List<Element<DocumentReference>> other;
}
