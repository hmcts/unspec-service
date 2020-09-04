package uk.gov.hmcts.reform.unspec.model.common.dynamiclist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicListElement {

    private final String code;
    private final String label;
}
