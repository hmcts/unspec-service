package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAuthContent {

    String userToken;
    String userId;
}