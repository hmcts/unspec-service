package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class Address {
    private final String addressLine1;
    private final String addressLine2;
    private final String addressLine3;
    private final String postTown;
    private final String county;
    private final String postcode;
    private final String country;
}
