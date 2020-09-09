package uk.gov.hmcts.reform.unspec.sampledata;

import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.Document;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;

import static java.time.LocalDateTime.of;

public class CaseDocumentBuilder {

    private String documentName;
    private DocumentType documentType;

    public static CaseDocumentBuilder builder() {
        return new CaseDocumentBuilder();
    }

    public CaseDocumentBuilder documentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public CaseDocumentBuilder documentType(DocumentType documentType) {
        this.documentType = documentType;
        return this;
    }

    public CaseDocument build() {
        return CaseDocument.builder()
            .documentLink(Document.builder()
                              .documentFileName(documentName)
                              .documentBinaryUrl(
                                  "http://dm-store:4506/documents/73526424-8434-4b1f-acca-bd33a3f8338f/binary")
                              .documentUrl("http://dm-store:4506/documents/73526424-8434-4b1f-acca-bd33a3f8338f")
                              .build())
            .documentSize(56975)
            .createdDatetime(of(2020, 7, 16, 14, 5, 15, 550439))
            .documentType(documentType)
            .createdBy("Unspec")
            .documentName(documentName)
            .build();
    }
}
