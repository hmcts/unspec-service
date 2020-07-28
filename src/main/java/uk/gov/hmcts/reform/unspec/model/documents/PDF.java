package uk.gov.hmcts.reform.unspec.model.documents;

import lombok.Data;
import org.springframework.http.MediaType;

@Data
public class PDF {

    public static final String CONTENT_TYPE = MediaType.APPLICATION_PDF_VALUE;
    public static final String EXTENSION = ".pdf";

    private final String fileBaseName;
    private final byte[] bytes;
    private final DocumentType documentType;

    public String getFilename() {
        return fileBaseName + EXTENSION;
    }
}
