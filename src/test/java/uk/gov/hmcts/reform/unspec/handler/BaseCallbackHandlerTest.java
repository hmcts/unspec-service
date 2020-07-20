package uk.gov.hmcts.reform.unspec.handler;

import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentMetadataDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams.Params;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.callback.CallbackVersion;
import uk.gov.hmcts.reform.unspec.service.UserService;

import java.util.Map;

public class BaseCallbackHandlerTest {
    @MockBean
    protected DocumentMetadataDownloadClientApi documentMetadataDownloadClient;
    @MockBean
    protected DocumentDownloadClientApi documentDownloadClient;
    @MockBean
    protected DocumentUploadClientApi documentUploadClient;
    @MockBean
    protected AuthTokenGenerator authTokenGenerator;
    @MockBean
    protected UserService userService;


    public CallbackParams callbackParamsOf(Map<String, Object> data, CallbackType type) {
        return callbackParamsOf(data, type, null, Map.of(Params.BEARER_TOKEN, "BEARER_TOKEN"));
    }

    public CallbackParams callbackParamsOf(Map<String, Object> data,
                                           CallbackType type,
                                           CallbackVersion version,
                                           Map<Params, Object> params

    ) {
        return CallbackParams.builder()
            .type(type)
            .request(toCallbackRequest(data))
            .version(version)
            .params(params)
            .build();
    }

    private CallbackRequest toCallbackRequest(Map<String, Object> data) {
        return CallbackRequest.builder()
            .caseDetails(CaseDetails.builder().data(data).id((Long) data.get("id")).build())
            .build();
    }
}
