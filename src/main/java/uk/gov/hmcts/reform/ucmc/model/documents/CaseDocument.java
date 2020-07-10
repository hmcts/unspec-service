package uk.gov.hmcts.reform.ucmc.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CaseDocument {
    private final String documentUrl;
    private final Document documentLink;
    private final String documentName;
    private final DocumentType documentType;
    private final LocalDateTime createdDatetime;
    private final String createdBy;
    private final long size;
}
