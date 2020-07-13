package uk.gov.hmcts.reform.ucmc.model.documents;

import org.springframework.http.MediaType;

@SuppressWarnings({"AbbreviationAsWordInName"})
public class PDF {
    public static final String CONTENT_TYPE = MediaType.APPLICATION_PDF_VALUE;
    public static final String EXTENSION = ".pdf";

    private final String fileBaseName;
    private final byte[] bytes;
    private final DocumentType documentType;

    public PDF(String fileBaseName, byte[] bytes, DocumentType documentType) {
        this.fileBaseName = fileBaseName;
        this.bytes = bytes;
        this.documentType = documentType;
    }

    public String getFilename() {
        return fileBaseName + EXTENSION;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public DocumentType getDocumentType() {
        return this.documentType;
    }
}
