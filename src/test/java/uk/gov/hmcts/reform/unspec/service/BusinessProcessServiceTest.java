package uk.gov.hmcts.reform.unspec.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.stateflow.model.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CREATE_CLAIM;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.FINISHED;
import static uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus.READY;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BusinessProcessService.class, JacksonAutoConfiguration.class})
public class BusinessProcessServiceTest {

    @Autowired
    private BusinessProcessService service;

    @ParameterizedTest
    @EnumSource(value = BusinessProcessStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"FINISHED"})
    void shouldAddErrorAndNotUpdateBusinessProcess_whenBusinessProcessStatusIsNotFinishedNorNull(BusinessProcessStatus
                                                                                          businessProcessStatus) {
        BusinessProcess businessProcess =  BusinessProcess.builder()
            .activityId("someActivityId")
            .processInstanceId("someProcessInstanceId")
            .status(businessProcessStatus)
            .build();
        Map<String, Object> data = new HashMap<>(Map.of("businessProcess", businessProcess));

        List<String> errors = service.updateBusinessProcess(data, CREATE_CLAIM);

        assertThat(errors).containsOnly("Business Process Error");
        assertThat(data).isEqualTo(Map.of("businessProcess", businessProcess));
    }

    @ParameterizedTest
    @EnumSource(value = BusinessProcessStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"FINISHED"})
    void shouldAddErrorAndNotSetStateFlowState_whenBusinessProcessStatusIsNotFinishedNorNull(BusinessProcessStatus
                                                                                          businessProcessStatus) {
        BusinessProcess businessProcess =  BusinessProcess.builder()
            .activityId("someActivityId")
            .processInstanceId("someProcessInstanceId")
            .status(businessProcessStatus)
            .build();
        Map<String, Object> data = new HashMap<>(Map.of("businessProcess", businessProcess));

        List<String> errors = service.updateBusinessProcess(data, CREATE_CLAIM, State.from("TEST.STATE"));

        assertThat(errors).containsOnly("Business Process Error");
        assertThat(data).isEqualTo(Map.of("businessProcess", businessProcess));
    }

    @ParameterizedTest
    @ArgumentsSource(GetBusinessProcessArguments.class)
    void shouldNotAddErrorAndUpdateBusinessProcess_whenBusinessProcessStatusFinishedOrNull(BusinessProcess
                                                                                               businessProcess) {
        Map<String, Object> data = new HashMap<>();
        data.put("businessProcess", businessProcess);

        List<String> errors = service.updateBusinessProcess(data, CREATE_CLAIM);

        assertThat(errors).isEmpty();
        assertThat(data).isEqualTo(Map.of(
            "businessProcess", BusinessProcess.builder().status(READY).camundaEvent(CREATE_CLAIM.name()).build()
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(GetBusinessProcessArguments.class)
    void shouldNotAddErrorAndUpdateStateFlowState_whenBusinessProcessStatusFinishedOrNull(BusinessProcess
                                                                                              businessProcess) {
        Map<String, Object> data = new HashMap<>();
        data.put("businessProcess", businessProcess);

        List<String> errors = service.updateBusinessProcess(data, CREATE_CLAIM, State.from("TEST.STATE"));

        assertThat(errors).isEmpty();
        assertThat(data).isEqualTo(Map.of(
            "businessProcess", BusinessProcess.builder().status(READY).camundaEvent(CREATE_CLAIM.name()).build(),
            "stateFlowState", "TEST.STATE")
        );
    }

    static class GetBusinessProcessArguments implements ArgumentsProvider {

        @Override
        @SneakyThrows
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(BusinessProcess.builder().status(FINISHED).build()),
                Arguments.of(BusinessProcess.builder().build()),
                Arguments.of((BusinessProcess) null)
            );
        }
    }

}
