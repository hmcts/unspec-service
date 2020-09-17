package uk.gov.hmcts.reform.unspec.config.properties.notification;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@Data
public class EmailTemplates {
    @NotEmpty
    private String defendantSolicitorClaimIssued;
}
