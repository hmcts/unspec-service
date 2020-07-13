package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DocumentReference {
    private final String documentUrl;
    private String documentFilename;
    private final String documentBinaryUrl;
}
