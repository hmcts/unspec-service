package uk.gov.hmcts.reform.camunda.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.camunda.model.StartInstanceRequestBody;
import uk.gov.hmcts.reform.camunda.model.StartInstanceResponse;
import uk.gov.hmcts.reform.unspec.config.FeignConfiguration;

@FeignClient(name = "camunda-api", url = "${camunda.api.url}", configuration = FeignConfiguration.class)
public interface CamundaApi {

    @PostMapping(value = "/engine-rest/process-definition/key/{key}/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    StartInstanceResponse startProcess(
        @PathVariable("key") String key,
        @RequestBody StartInstanceRequestBody variables
    );
}
