package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import org.w3c.dom.Document;

@Data
@Builder
public class ResponseDocument {

    private final Document file;
}
