package uk.gov.hmcts.reform.unspec.handler.callback;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.DISPATCHED;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.READY;

@SpringBootTest(classes = {
    DispatchBusinessProcessCallbackHandler.class,
    JacksonAutoConfiguration.class,
})
class DispatchBusinessProcessCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private DispatchBusinessProcessCallbackHandler handler;

    @Nested
    class AboutToSubmitCallback {

        @Test
        void shouldDispatchBusinessProcess_whenStatusIsReady() {
            CallbackParams params = callbackParamsOf(
                new HashMap<>(Map.of("businessProcess", BusinessProcess.builder().status(READY).build())),
                CallbackType.ABOUT_TO_SUBMIT
            );

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData()).extracting("businessProcess").extracting("status").isEqualTo(DISPATCHED);
        }

        @ParameterizedTest
        @EnumSource(value = BusinessProcessStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"READY"})
        void shouldNotDispatchBusinessProcess_whenStatusIsNotReady(BusinessProcessStatus status) {
            CallbackParams params = callbackParamsOf(
                new HashMap<>(Map.of("businessProcess", BusinessProcess.builder().status(status).build())),
                CallbackType.ABOUT_TO_SUBMIT
            );

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData()).extracting("businessProcess").extracting("status").isEqualTo(status);
        }
    }
}
