package uk.gov.hmcts.reform.ucmc.fnp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.ucmc.fnp.model.payment.CreditAccountPaymentRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi.SERVICE_AUTHORIZATION;

@FeignClient(name = "payment-api", url = "${payment.api.url}")
public interface PaymentApi {
    @PostMapping("/credit-account-payments")
    PaymentDto createCreditAccountPayment(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestBody CreditAccountPaymentRequest creditAccountPaymentRequest
    );
}
