package uk.gov.hmcts.reform.unspec.sampledata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.enums.CaseState;
import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.Map;

import static uk.gov.hmcts.reform.unspec.enums.CaseState.AWAITING_CASE_DETAILS_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.AWAITING_CASE_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.AWAITING_CLAIMANT_INTENTION;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.CREATED;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.PENDING_CASE_ISSUED;

@SuppressWarnings("unchecked")
public class CaseDetailsBuilder {

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String state;
    private Map<String, Object> data;
    private Long id;

    public static CaseDetailsBuilder builder() {
        return new CaseDetailsBuilder();
    }

    public CaseDetailsBuilder state(CaseState state) {
        this.state = state.name();
        return this;
    }

    public CaseDetailsBuilder data(CaseData caseData) {
        this.data = mapper.convertValue(caseData, Map.class);
        return this;
    }

    public CaseDetailsBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CaseDetailsBuilder atStateClaimDraft() {
        CaseData caseData = CaseDataBuilder.builder().atStatePendingCaseIssued().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = PENDING_CASE_ISSUED.name();
        return this;
    }

    public CaseDetailsBuilder atStateAwaitingCaseNotification() {
        CaseData caseData = CaseDataBuilder.builder().atStateAwaitingCaseNotification().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = AWAITING_CASE_NOTIFICATION.name();
        return this;
    }

    public CaseDetailsBuilder atStateAwaitingCaseDetailsNotification() {
        CaseData caseData = CaseDataBuilder.builder().atStateAwaitingCaseDetailsNotification().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = AWAITING_CASE_DETAILS_NOTIFICATION.name();
        return this;
    }

    public CaseDetailsBuilder atStateClaimCreated() {
        CaseData caseData = CaseDataBuilder.builder().atStateClaimCreated().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = CREATED.name();
        return this;
    }

    public CaseDetailsBuilder atStateServiceAcknowledge() {
        CaseData caseData = CaseDataBuilder.builder().atStateServiceAcknowledge().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = CREATED.name();
        return this;
    }

    public CaseDetailsBuilder atStateRespondedToClaim() {
        CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullDefence().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = AWAITING_CLAIMANT_INTENTION.name();
        return this;
    }

    public CaseDetailsBuilder atStateFullDefence() {
        CaseData caseData = CaseDataBuilder.builder().atStateApplicantRespondToDefence().build();
        this.data = mapper.convertValue(caseData, Map.class);
        this.state = AWAITING_CLAIMANT_INTENTION.name();
        return this;
    }

    public CaseDetails build() {
        return CaseDetails.builder()
            .data(data)
            .state(state)
            .id(id)
            .build();
    }
}
