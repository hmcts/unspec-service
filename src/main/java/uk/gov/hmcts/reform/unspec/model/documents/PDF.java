package uk.gov.hmcts.reform.unspec.model.documents;

import lombok.Data;

@Data
public class PDF {

    public static final String EXTENSION = ".pdf";

    private final String fileBaseName;
    private final byte[] bytes;
    private final DocumentType documentType;

    public String getFilename() {
        return fileBaseName + EXTENSION;
    }
}
