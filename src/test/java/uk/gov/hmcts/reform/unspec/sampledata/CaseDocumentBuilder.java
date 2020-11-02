package uk.gov.hmcts.reform.unspec.sampledata;

import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;

import java.time.LocalDateTime;

public class CaseDocumentBuilder {

    private String documentName;
    private DocumentType documentType;
    private LocalDateTime createdDatetime = LocalDateTime.of(2020, 7, 16, 14, 5, 15, 550439);
    private String createdBy = "Unspec";
    private long documentSize = 56975;

    public static CaseDocumentBuilder builder() {
        return new CaseDocumentBuilder();
    }

    public CaseDocumentBuilder of(DocumentType documentType, LocalDateTime createdDatetime) {
        this.documentType = documentType;
        this.createdDatetime = createdDatetime;
        return this;
    }

    public CaseDocumentBuilder documentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public CaseDocumentBuilder documentType(DocumentType documentType) {
        this.documentType = documentType;
        return this;
    }

    public CaseDocumentBuilder createdDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
        return this;
    }

    public CaseDocument build() {
        return CaseDocument.builder()
            .documentLink(DocumentBuilder.builder().documentName(documentName).build())
            .createdDatetime(createdDatetime)
            .documentType(documentType)
            .documentName(documentName)
            .createdBy(createdBy)
            .documentSize(documentSize)
            .build();
    }
}
