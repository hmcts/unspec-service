package uk.gov.hmcts.reform.ccd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
@Builder
public class CaseAssignmentUserRolesRequest {

    @JsonProperty("case_users")
    private List<CaseAssignmentUserRoleWithOrganisation> caseAssignmentUserRolesWithOrganisation;

}
