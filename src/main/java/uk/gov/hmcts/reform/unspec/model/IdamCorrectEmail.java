package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.YesOrNo;

@Data
@Builder
public class IdamCorrectEmail {

    private final String email;
    private final YesOrNo isCorrect;

    public boolean isCorrect() {
        return isCorrect == YesOrNo.YES;
    }
}
