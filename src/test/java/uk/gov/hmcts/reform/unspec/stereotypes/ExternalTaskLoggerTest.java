package uk.gov.hmcts.reform.unspec.stereotypes;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.config.AspectConfiguration;

@ExtendWith(SpringExtension.class)
class ExternalTaskLoggerTest {

    @Mock
    JoinPoint proceedingJoinPoint;
    @InjectMocks
    ExternalTaskLogger externalTaskLogger;

}
