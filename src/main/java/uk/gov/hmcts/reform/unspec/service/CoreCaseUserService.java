package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CaseUserApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseUser;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.unspec.config.SystemUpdateUserConfiguration;

@Service
@RequiredArgsConstructor
public class CoreCaseUserService {
    private final CaseUserApi caseUserApi;
    private final IdamClient idamClient;
    private final SystemUpdateUserConfiguration systemUpdateUserConfiguration;
    private final AuthTokenGenerator authTokenGenerator;

    public void assignCase(String caseId, CaseUser caseUser) {

        String accessToken = idamClient.getAccessToken(
            systemUpdateUserConfiguration.getUserName(),
            systemUpdateUserConfiguration.getPassword()
        );

        caseUserApi.updateCaseRolesForUser(
            accessToken,
            authTokenGenerator.generate(),
            caseId,
            caseUser.getUserId(),
            caseUser
        );
    }
}
