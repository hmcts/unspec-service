package uk.gov.hmcts.reform.ccd.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.hmcts.reform.ccd.model.CaseAssignmentUserRolesRequest;
import uk.gov.hmcts.reform.ccd.model.CaseAssignmentUserRolesResource;
import uk.gov.hmcts.reform.ccd.model.CaseAssignmentUserRolesResponse;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(
        name = "core-case-data-api",
        url = "${core_case_data.api.url}",
        configuration = CoreCaseDataConfiguration.class
)
public interface CaseAssignmentApi {

    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @PostMapping(
            value = "/case-users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    CaseAssignmentUserRolesResponse addCaseUserRoles(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestBody CaseAssignmentUserRolesRequest caseRoleRequest
    );

    @GetMapping(
            value = "/case-users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    CaseAssignmentUserRolesResource getUserRoles(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestParam("case_ids") List<String> caseIds
    );

    @DeleteMapping(
            value = "/case-users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    CaseAssignmentUserRolesResponse removeCaseUserRoles(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestBody CaseAssignmentUserRolesRequest caseRoleRequest
    );
}
