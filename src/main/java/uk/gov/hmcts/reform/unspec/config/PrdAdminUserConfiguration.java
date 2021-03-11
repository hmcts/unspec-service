package uk.gov.hmcts.reform.unspec.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PrdAdminUserConfiguration {

    private final String userName;
    private final String password;

    public PrdAdminUserConfiguration(@Value("${unspecified.prd-adminga.username}") String userName,
                                     @Value("${unspecified.prd-adminga.password}") String password) {
        this.userName = userName;
        this.password = password;
    }
}
