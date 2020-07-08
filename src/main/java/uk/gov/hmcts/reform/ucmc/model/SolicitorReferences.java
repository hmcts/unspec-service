package uk.gov.hmcts.reform.ucmc.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class SolicitorReferences {
    private final String solicitorReference;
    private final String defendantReference;
}
