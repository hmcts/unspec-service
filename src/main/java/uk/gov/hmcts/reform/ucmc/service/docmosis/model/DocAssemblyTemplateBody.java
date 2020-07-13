package uk.gov.hmcts.reform.ucmc.service.docmosis.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Value;
import uk.gov.hmcts.reform.docassembly.domain.FormPayload;
import uk.gov.hmcts.reform.ucmc.model.Applicant;
import uk.gov.hmcts.reform.ucmc.model.StatementOfTruth;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocAssemblyTemplateBody implements FormPayload {
    @JsonProperty("courtseal")
    private final String courtSeal = "[userImage:courtseal.PNG]";
    private List<Applicant> claimant;
    private List<Applicant> defendants;
    private String referenceNumber;
    private String feeAccount;
    private String externalReferenceNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate submittedOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate issueDate;
    private String claimDetails;
    private String statementOfValue;
    private String claimAmount;
    private String legalRepCost;
    private String courtFee;
    private String hearingCourtName;
    private StatementOfTruth statementOfTruth;
}
