package uk.gov.hmcts.reform.ucmc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.ucmc.enums.ServiceMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.String.format;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.formatLocalDate;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.formatLocalDateTime;

@Api
@RestController
@RequestMapping("/confirm-service")
public class ConfirmServiceController {

    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/submitted")
    public SubmittedCallbackResponse handleSubmitted(@RequestBody CallbackRequest callbackRequest) {
        Map<String, Object> data = callbackRequest.getCaseDetails().getData();
        ServiceMethod serviceMethod = mapper.convertValue(data.get("serviceMethod"), ServiceMethod.class);

        // TODO: this field will be different when date / date time in CCD.
        LocalDate serviceTime = mapper.convertValue(data.get("serviceDate"), LocalDate.class);
        LocalDate deemedDateOfService = serviceMethod.getDeemedDateOfService(serviceTime);

        String formattedDeemedDateOfService = formatLocalDate(deemedDateOfService, DATE);
        String responseDeadlineDate = formatLocalDateTime(addFourteenDays(deemedDateOfService), DATE_TIME_AT);
        String certificateOfServiceLink = "http://www.google.com";

        String body = format("<br /> Deemed date of service: %s."
                                 + "<br />The defendant must respond before %s."
                                 + "\n\n[Download certificate of service](%s) (PDF, 266 KB)",
                             formattedDeemedDateOfService, responseDeadlineDate, certificateOfServiceLink
        );

        return SubmittedCallbackResponse.builder()
            .confirmationHeader("# You've confirmed service")
            .confirmationBody(body)
            .build();
    }

    private LocalDateTime addFourteenDays(LocalDate deemedDateOfService) {
        return deemedDateOfService.plusDays(14).atTime(16, 0);
    }
}
