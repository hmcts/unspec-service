package uk.gov.hmcts.reform.prd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DxAddress {

    private String dxExchange;
    private String dxNumber;
}
