package uk.gov.hmcts.reform.ucmc.model.docmosis.sealedclaim;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.ucmc.model.Address;

@Data
@Builder(toBuilder = true)
public class Defendant {
    private final String name;
    private final Address primaryAddress;
    private final Representative representative;
}
