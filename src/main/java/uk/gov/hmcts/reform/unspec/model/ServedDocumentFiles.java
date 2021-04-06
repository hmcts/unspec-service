package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.Document;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Data
@Builder
public class ServedDocumentFiles {

    private List<Element<Document>> other;
    private List<Element<Document>> medicalReports;
    private List<Element<Document>> scheduleOfLoss;
    private List<Element<Document>> particularsOfClaimFile;
    private String particularsOfClaimText;
    private List<Element<Document>> certificateOfSuitability;

    @JsonIgnore
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();
        if (ofNullable(particularsOfClaimFile).isPresent() && ofNullable(particularsOfClaimText).isPresent()) {
            errors.add("You need to either upload 1 Particulars of claim only or enter the Particulars "
                           + "of claim text in the field provided. You cannot do both.");
        }

        if (ofNullable(particularsOfClaimFile).isEmpty() && ofNullable(particularsOfClaimText).isEmpty()) {
            errors.add("You must add Particulars of claim details");
        }
        return errors;
    }
}
