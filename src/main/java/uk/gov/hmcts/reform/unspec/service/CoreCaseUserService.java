package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CaseAccessDataStoreApi;
import uk.gov.hmcts.reform.ccd.model.AddCaseAssignedUserRolesRequest;
import uk.gov.hmcts.reform.ccd.model.CaseAssignedUserRoleWithOrganisation;
import uk.gov.hmcts.reform.ccd.model.CaseAssignedUserRolesRequest;
import uk.gov.hmcts.reform.ccd.model.CaseAssignedUserRolesResource;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.unspec.config.CrossAccessUserConfiguration;
import uk.gov.hmcts.reform.unspec.enums.CaseRole;

import java.util.List;

import static uk.gov.hmcts.reform.unspec.enums.CaseRole.CREATOR;

@Service
@RequiredArgsConstructor
public class CoreCaseUserService {

    Logger log = LoggerFactory.getLogger(CoreCaseUserService.class);

    private final CaseAccessDataStoreApi caseAccessDataStoreApi;
    private final IdamClient idamClient;
    private final CrossAccessUserConfiguration crossAccessUserConfiguration;
    private final AuthTokenGenerator authTokenGenerator;

    public void assignCaseToDefendant(String caseId, String userId, String organisationId) {
        assignUserToCaseForRole(caseId, userId, organisationId, CaseRole.RESPONDENTSOLICITORONE);
    }

    public void assignCaseToClaimant(String caseId, String userId, String organisationId) {
        if (!userHasCaseRole(caseId, CaseRole.APPLICANTSOLICITORONE)) {
            assignUserToCaseForRole(caseId, userId, organisationId, CaseRole.APPLICANTSOLICITORONE);
        } else {
            log.info("Case already have the user with {} role", CaseRole.APPLICANTSOLICITORONE.getFormattedName());
        }
    }

    public void removeCreatorRoleCaseAssignment(String caseId, String userId, String organisationId) {
        if (userHasCaseRole(caseId, CREATOR)) {
            removeCreatorAccess(caseId, userId, organisationId);
        } else {
            log.info("User doesn't have {} role", CREATOR.getFormattedName());
        }
    }

    public void assignUserToCaseForRole(String caseId, String userId, String organisationId, CaseRole caseRole) {
        CaseAssignedUserRoleWithOrganisation caseAssignedUserRoleWithOrganisation
            = CaseAssignedUserRoleWithOrganisation.builder()
            .caseDataId(caseId)
            .userId(userId)
            .caseRole(caseRole.getFormattedName())
            .organisationId(organisationId)
            .build();

        caseAccessDataStoreApi.addCaseUserRoles(
            getCaaAccessToken(),
            authTokenGenerator.generate(),
            AddCaseAssignedUserRolesRequest.builder()
                .caseAssignedUserRoles(List.of(caseAssignedUserRoleWithOrganisation))
                .build()
        );
    }

    private void removeCreatorAccess(String caseId, String userId, String organisationId) {
        CaseAssignedUserRoleWithOrganisation caseAssignedUserRoleWithOrganisation
            = CaseAssignedUserRoleWithOrganisation.builder()
            .caseDataId(caseId)
            .userId(userId)
            .caseRole(CREATOR.getFormattedName())
            .organisationId(organisationId)
            .build();

        caseAccessDataStoreApi.removeCaseUserRoles(
            getCaaAccessToken(),
            authTokenGenerator.generate(),
            CaseAssignedUserRolesRequest.builder()
                .caseAssignedUserRoles(List.of(caseAssignedUserRoleWithOrganisation))
                .build()
        );
    }

    private boolean userHasCaseRole(String caseId, CaseRole caseRole) {
        CaseAssignedUserRolesResource userRoles = caseAccessDataStoreApi.getUserRoles(
            getCaaAccessToken(),
            authTokenGenerator.generate(),
            List.of(caseId)
        );

        return userRoles.getCaseAssignedUserRoles().stream()
            .anyMatch(c -> c.getCaseRole().equals(caseRole.getFormattedName()));
    }

    private String getCaaAccessToken() {
        return idamClient.getAccessToken(
            crossAccessUserConfiguration.getUserName(),
            crossAccessUserConfiguration.getPassword()
        );
    }
}
