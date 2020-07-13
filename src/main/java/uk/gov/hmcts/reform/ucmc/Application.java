package uk.gov.hmcts.reform.ucmc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "uk.gov.hmcts")
@EnableCircuitBreaker
@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.idam.client",
    "uk.gov.hmcts.reform.ccd.client",
    "uk.gov.hmcts.reform.authorisation.generators",
    "uk.gov.hmcts.reform.document",
    "uk.gov.hmcts.reform.docassembly"
})
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
