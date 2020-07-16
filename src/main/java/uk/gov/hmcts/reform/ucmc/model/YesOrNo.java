package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum YesOrNo {
    @JsonProperty("Yes")
    YES,
    @JsonProperty("No")
    NO
}
