package uk.gov.hmcts.reform.ucmc.service.docmosis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Claimant {
    private String name;
    private Address address;
    private Address serviceAddress;
}
