package uk.gov.hmcts.reform.ucmc.model;

import uk.gov.hmcts.reform.ucmc.model.common.Element;

import java.util.List;

public class ServedDocumentFiles {
    List<Element<DocumentReference>> particularsOfClaim;
    List<Element<DocumentReference>> medicalReports;
    List<Element<DocumentReference>> scheduleOfLoss;
    List<Element<DocumentReference>> certificateOfSuitability;
    List<Element<DocumentReference>> other;
}
