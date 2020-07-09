package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
    @JsonProperty("AddressLine1")
    private String addressLine1;
    @JsonProperty("AddressLine2")
    private String addressLine2;
    @JsonProperty("AddressLine3")
    private String addressLine3;
    @JsonProperty("PostTown")
    private String postTown;
    @JsonProperty("PostCode")
    private String postCode;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("County")
    private String county;
}
