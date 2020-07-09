package uk.gov.hmcts.reform.ucmc.service.docmosis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postTown;
    private String postCode;
    private String country;
}
