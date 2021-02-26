package uk.gov.hmcts.reform.unspec.service.robotics.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.unspec.enums.YesOrNo;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.robotics.Event;
import uk.gov.hmcts.reform.unspec.model.robotics.EventDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.EventHistory;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.flowstate.FlowState;
import uk.gov.hmcts.reform.unspec.service.flowstate.StateFlowEngine;

import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class,
    StateFlowEngine.class,
    EventHistoryMapper.class
})
class EventHistoryMapperTest {

    private static final Event EMPTY_EVENT = Event.builder().build();

    @Autowired
    EventHistoryMapper mapper;

    @Test
    void shouldPrepareMiscellaneousEvent_whenClaimWithUnrepresentedDefendant() {
        CaseData caseData = CaseDataBuilder.builder().atStateClaimDraft()
            .respondent1Represented(YesOrNo.NO)
            .build();
        Event expectedEvent = Event.builder()
            .eventSequence(1)
            .eventCode("999")
            .dateReceived(caseData.getClaimSubmittedDateTime().toLocalDate().format(ISO_DATE))
            .eventDetails(EventDetails.builder()
                              .miscText("RPA Reason: Unrepresented defendant.")
                              .build())
            .build();

        var eventHistory = mapper.buildEvents(caseData);

        assertThat(eventHistory).isNotNull();
        assertThat(eventHistory)
            .extracting("miscellaneous")
            .asList()
            .hasSize(1)
            .containsExactly(expectedEvent);
        assertEmptyEvents(
            eventHistory,
            "acknowledgementOfServiceReceived",
            "consentExtensionFilingDefence",
            "defenceFiled",
            "defenceAndCounterClaim",
            "receiptOfPartAdmission",
            "receiptOfAdmission",
            "replyToDefence",
            "directionsQuestionnaireFiled"
        );
    }

    @ParameterizedTest
    @EnumSource(value = FlowState.Main.class, mode = EnumSource.Mode.EXCLUDE, names = {
        "RESPONDENT_FULL_ADMISSION",
        "RESPONDENT_PART_ADMISSION",
        "RESPONDENT_COUNTER_CLAIM",
        "PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT"
    })
    void shouldBuildEmptyEventHistory_whenNoMappingsDefinedForStateFlow(FlowState.Main flowStateMain) {
        CaseData caseData = CaseDataBuilder.builder().atState(flowStateMain).build();


        var eventHistory = mapper.buildEvents(caseData);

        assertThat(eventHistory).isNotNull();
        assertEmptyEvents(
            eventHistory,
            "miscellaneous",
            "acknowledgementOfServiceReceived",
            "consentExtensionFilingDefence",
            "defenceFiled",
            "defenceAndCounterClaim",
            "receiptOfPartAdmission",
            "receiptOfAdmission",
            "replyToDefence",
            "directionsQuestionnaireFiled"
        );
    }

    private void assertEmptyEvents(EventHistory eventHistory, String... eventNames) {
        Stream.of(eventNames).forEach(
            eventName -> assertThat(eventHistory).extracting(eventName).asList().containsOnly(EMPTY_EVENT));
    }
}
