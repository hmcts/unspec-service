package uk.gov.hmcts.reform.unspec.model.common.dynamiclist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DynamicList {

    private DynamicListElement value;
    @JsonProperty("list_items")
    private List<DynamicListElement> listItems;
}
