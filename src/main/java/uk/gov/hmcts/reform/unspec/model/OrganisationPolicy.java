package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganisationPolicy {

    @JsonProperty("Organisation")
    private final Organisation organisation;

    @JsonProperty("OrgPolicyReference")
    private final String orgPolicyReference;

    @JsonProperty("OrgPolicyCaseAssignedRole")
    private final String orgPolicyCaseAssignedRole;
}
